package org.werk2.core.config.cycles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.werk2.config.Werk2Config;
import org.werk2.config.calls.BatchCall;
import org.werk2.config.calls.Call;
import org.werk2.config.engine.Engine;
import org.werk2.config.entities.Event;
import org.werk2.config.entities.Exec;
import org.werk2.config.entities.ExtendedFlow;
import org.werk2.config.entities.ExtendedStep;
import org.werk2.config.entities.Flow;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.entities.Step;
import org.werk2.config.entities.StepCall;
import org.werk2.config.entities.Transit;
import org.werk2.config.functions.Function;
import org.werk2.core.config.RawFunction;

public class GraphBuilder {
	
	/*
CallIndirectionGraph

{definition}
[[call]]

Flow
	{function->functionName}
	1 firstStep StepCall
	* step StepCall
	* listener Listener

Step
	{function->functionName}
	1 execBlock BatchCall
	1 transit Call
	* listener Listener

Exec
	{execFunctionName}
	[[calledFunctionName]]
	* listener Listener

Transit
	{transitFunctionName}
	[[calledFunctionName]]
	* listener Listener

ExtendedFlow
	{newFunctionName}
	1 newFirstStep StepCall
	* addStep StepCall
	* addListener Listener
	
ExtendedStep
	{newFunctionName}
	* addExecBlock BatchCall
	1 newTransit Call
	* addListener Listener

-------------------------------------------------

	Listener
	StepCall
	BatchCall
	Call

Listener -> BatchCall
StepCall -> BatchCall
BatchCall -> Call

Call -> [[functionName]]

	 */
	protected void addFunction(GraphNode node, String functionName) {
		node.getLinks().add(functionName);
	}
	
	protected void addCall(GraphNode node, Call call) {
		addFunction(node, call.getFunctionName());
	}
	
	protected void addBatchCall(GraphNode node, BatchCall batchCall) {
		if (!batchCall.getCalls().isEmpty())
		for (Call call : batchCall.getCalls().get())
			addCall(node, call);
		
		if (!batchCall.getBatches().isEmpty())
		for (BatchCall batch : batchCall.getBatches().get())
			addBatchCall(node, batch);

		node.getLinks().add(null);
	}
	
	protected void addListenerCall(GraphNode node, ListenerCall listenerCall, Event[] events) {
		Set<Event> eventsSet = new HashSet<>();
		for (Event event : events)
			eventsSet.add(event);
		for (Event event : listenerCall.getEvents()) {
			if (eventsSet.contains(event)) {
				addBatchCall(node, listenerCall);
				break;
			}
		}
	}
	
	class Ownership {
		Engine engine;
		Flow flow;
		Step step;
		ExtendedFlow eFlow;
		ExtendedStep eStep;
		Transit transit;
		Exec exec;
		
	}
	
	//TODO: Tree structure for listeners: Engine-defined Listeners, Flow-defined Listeners, Step-defined Listeners
	//		should be applied to all sub-Entities, unless overridden
	public List<GraphNode> buildGraph(List<Werk2Config> configs) {
		//Maps of steps and flows for lookup by functionName
		Map<String, Step> steps = new HashMap<>();
		Map<String, ExtendedStep> extendedSteps = new HashMap<>();
		
		Map<String, Flow> flows = new HashMap<>();
		Map<String, ExtendedFlow> extendedFlows = new HashMap<>();
		
		for (Werk2Config config : configs) {
			if (!config.getFlows().isEmpty())
			for (Flow flow : config.getFlows().get())
				flows.put(flow.getFunction().getFunctionName(), flow);
			
			if (!config.getSteps().isEmpty())
			for (Step step : config.getSteps().get())
				steps.put(step.getFunction().getFunctionName(), step);

			if (!config.getExtendedFlows().isEmpty())
			for (ExtendedFlow eFlow : config.getExtendedFlows().get())
				extendedFlows.put(eFlow.getNewFunctionName(), eFlow);

			if (!config.getExtendedSteps().isEmpty())
			for (ExtendedStep eStep : config.getExtendedSteps().get())
				extendedSteps.put(eStep.getNewFunctionName(), eStep);
		}
		
		//TODO:
		//Raw functions don't call listeners
		//Listener summation: Engine -> Flow -> Step -> Transit
		//                                          \-> ExecBlock -> Exec
		
		List<GraphNode> retList = new ArrayList<>();
		for (Werk2Config config : configs) {
			//Raw Functions
			if (!config.getRawFunctions().isEmpty())
			for (Function rawFunction : config.getRawFunctions().get()) {
				GraphNode node = new GraphNode(rawFunction.getFunctionName());
				retList.add(node);
			}
			
			//Flows
			if (!config.getFlows().isEmpty())
			for (Flow flow : config.getFlows().get()) {
				GraphNode node = new GraphNode(flow.getFunction().getFunctionName());
				retList.add(node);
				
				addCall(node, flow.getFirstStep());

				if (!flow.getSteps().isEmpty())
					for (StepCall step : flow.getSteps().get())
						addCall(node, step);

				if (!flow.getListeners().isEmpty())
					for (ListenerCall listener : flow.getListeners().get())
						addListenerCall(node, listener, new Event[] { Event.FLOW_STARTED, Event.FLOW_FINISHED });
			}
			
			//Step
			if (!config.getSteps().isEmpty())
			for (Step step : config.getSteps().get()) {
				GraphNode node = new GraphNode(step.getFunction().getFunctionName());
				retList.add(node);
				
				addCall(node, step.getTransit());

				if (!step.getExecBlock().isEmpty())
					addBatchCall(node, step.getExecBlock().get());

				if (!step.getListeners().isEmpty())
					for (ListenerCall listener : step.getListeners().get())
						addListenerCall(node, listener, new Event[] { Event.STEP_STARTED, Event.STEP_FINISHED });
			}

			//Exec
			if (!config.getExecs().isEmpty())
			for (Exec exec : config.getExecs().get()) {
				GraphNode node = new GraphNode(exec.getExecFunctionName());
				retList.add(node);
				
				addFunction(node, exec.getCalledFunctionName());

				if (!exec.getListeners().isEmpty())
					for (ListenerCall listener : exec.getListeners().get())
						addListenerCall(node, listener, new Event[] { Event.EXECUTOR_STARTED, Event.EXECUTOR_FINISHED });
			}

			//Transit
			if (!config.getTransits().isEmpty())
			for (Transit transit : config.getTransits().get()) {
				GraphNode node = new GraphNode(transit.getTransitFunctionName());
				retList.add(node);
				
				addFunction(node, transit.getCalledFunctionName());

				if (!transit.getListeners().isEmpty())
					for (ListenerCall listener : transit.getListeners().get())
						addListenerCall(node, listener, new Event[] { Event.TRANSITIONER_STARTED, Event.TRANSITIONER_FINISHED });
			}

			//Extended Flow
			//FlowInheritance: Steps and Listeners from Super-Flow declarations 
			//should be applied to ExtendedFlows recursively, unless overridden.
			if (!config.getExtendedFlows().isEmpty())
			for (ExtendedFlow eFlow : config.getExtendedFlows().get()) {
				GraphNode node = new GraphNode(eFlow.getNewFunctionName());
				retList.add(node);

				//First step
				if (!eFlow.getNewFirstStep().isEmpty()) {
					addCall(node, eFlow.getNewFirstStep().get());
				} else {
					ExtendedFlow current = eFlow;
					boolean firstStepAssigned = false;
					while (extendedFlows.containsKey(current.getSuperFlowFunctionName())) {
						current = extendedFlows.get(current.getSuperFlowFunctionName());
						if (!current.getNewFirstStep().isEmpty()) {
							addCall(node, current.getNewFirstStep().get());
							firstStepAssigned = true;
							break;
						}
						if (!firstStepAssigned) {
							Flow rootFlow = flows.get(current.getSuperFlowFunctionName());
							if (!firstStepAssigned)
								addCall(node, rootFlow.getFirstStep());
						}
					}
				}


				//Steps
				if (!eFlow.getAddSteps().isEmpty())
					for (StepCall step : eFlow.getAddSteps().get())
						addCall(node, step);

				//Steps from extended super-flow declarations (recursive)
				if (eFlow.getDropOldSteps().isEmpty() || !eFlow.getDropOldSteps().get()) {
					ExtendedFlow current = eFlow;
					boolean drop = false;
					while (extendedFlows.containsKey(current.getSuperFlowFunctionName())) {
						current = extendedFlows.get(current.getSuperFlowFunctionName());
						if (!current.getAddSteps().isEmpty())
							for (StepCall step : current.getAddSteps().get())
								addCall(node, step);
						
						//Step inheritance can also be dropped at super-flow stage
						if (!current.getDropOldSteps().isEmpty() && current.getDropOldSteps().get()) {
							drop = true;
							break;
						}
					}
					
					if (!drop) {
						//add Steps from root super-flow declaration
						Flow rootFlow = flows.get(current.getSuperFlowFunctionName());
						if (!rootFlow.getSteps().isEmpty())
							for (StepCall step : rootFlow.getSteps().get())
								addCall(node, step);
					}
				}
				
				//Add listeners for flow events (FLOW_STARTED, FLOW_FINISHED)
				if (!eFlow.getAddListeners().isEmpty())
					for (ListenerCall listener : eFlow.getAddListeners().get())
						addListenerCall(node, listener, new Event[] { Event.FLOW_STARTED, Event.FLOW_FINISHED });

				//Listeners from super-flow declarations (recursive)
				if (eFlow.getDropOldListeners().isEmpty() || !eFlow.getDropOldListeners().get()) {
					ExtendedFlow current = eFlow;
					boolean drop = false;
					while (extendedFlows.containsKey(current.getSuperFlowFunctionName())) {
						current = extendedFlows.get(current.getSuperFlowFunctionName());
						if (!current.getAddListeners().isEmpty())
							for (ListenerCall listener : current.getAddListeners().get())
								addListenerCall(node, listener, new Event[] { Event.FLOW_STARTED, Event.FLOW_FINISHED });
						
						//Listener inheritance also can be dropped at super-flow stage
						if (!current.getDropOldListeners().isEmpty() && current.getDropOldListeners().get()) {
							drop = true;
							break;
						}
					}
					if (!drop) {
						//add Listeners from root super-flow declaration
						Flow rootFlow = flows.get(current.getSuperFlowFunctionName());
						if (!rootFlow.getListeners().isEmpty())
							for (ListenerCall listener : rootFlow.getListeners().get())
								addListenerCall(node, listener, new Event[] { Event.FLOW_STARTED, Event.FLOW_FINISHED });
					}
				}
			}

			//Extended Step
			//Step Inheritance: Execs, Transit, Listeners from super-Step declarations 
			//should be applied to ExtendedSteps recursively, unless overridden
			if (!config.getExtendedSteps().isEmpty())
			for (ExtendedStep eStep : config.getExtendedSteps().get()) {
				GraphNode node = new GraphNode(eStep.getNewFunctionName());
				retList.add(node);

				//Add transit
				if (!eStep.getNewTransit().isEmpty()) {
					addCall(node, eStep.getNewTransit().get());
				} else {
					ExtendedStep current = eStep;
					boolean transitAssigned = false;
					while (extendedSteps.containsKey(current.getSuperStepFunctionName())) {
						//try to add transit from extended super-steps
						current = extendedSteps.get(current.getSuperStepFunctionName());
						if (!current.getNewTransit().isEmpty()) {
							addCall(node, current.getNewTransit().get());
							transitAssigned = true;
							break;
						}
						//if not defined, add transit from root super-step declaration
						if (!transitAssigned) {
							Step rootStep = steps.get(current.getSuperStepFunctionName());
							addCall(node, rootStep.getTransit());
						}
					}
				}
				
				//Add exec
				if (!eStep.getAddExecBlock().isEmpty())
					addBatchCall(node, eStep.getAddExecBlock().get());
				
				if (eStep.getDropOldExecBlock().isEmpty() || !eStep.getDropOldExecBlock().get()) {
					ExtendedStep current = eStep;
					boolean drop = false;
					while (extendedSteps.containsKey(current.getSuperStepFunctionName())) {
						current = extendedSteps.get(current.getSuperStepFunctionName());
						if (!current.getAddExecBlock().isEmpty())
							addBatchCall(node, current.getAddExecBlock().get());
						
						//Exec inheritance can also be dropped at super-step stage
						if (!current.getDropOldExecBlock().isEmpty() && current.getDropOldExecBlock().get()) {
							drop = true;
							break;
						}
					}
					
					if (!drop) {
						//add exec from root super-step declaration
						Step rootStep = steps.get(current.getSuperStepFunctionName());
						if (!rootStep.getExecBlock().isEmpty())
							addBatchCall(node, rootStep.getExecBlock().get());
					}
				}

				//Add listeners for step events (STEP_STARTED, STEP_FINISHED)
				if (!eStep.getAddListeners().isEmpty())
					for (ListenerCall listener : eStep.getAddListeners().get())
						addListenerCall(node, listener, new Event[] { Event.STEP_STARTED, Event.STEP_FINISHED });
				
				//Listeners from super-step declarations (recursive)
				if (eStep.getDropOldListeners().isEmpty() || !eStep.getDropOldListeners().get()) {
					ExtendedStep current = eStep;
					boolean drop = false;
					while (extendedSteps.containsKey(current.getSuperStepFunctionName())) {
						current = extendedSteps.get(current.getSuperStepFunctionName());
						if (!current.getAddListeners().isEmpty())
							for (ListenerCall listener : current.getAddListeners().get())
								addListenerCall(node, listener, new Event[] { Event.STEP_STARTED, Event.STEP_FINISHED });
						
						//Listener inheritance also can be dropped at super-step stage
						if (!current.getDropOldListeners().isEmpty() && current.getDropOldListeners().get()) {
							drop = true;
							break;
						}
					}
					if (!drop) {
						//add Listeners from root super-step declaration
						Step rootStep = steps.get(current.getSuperStepFunctionName());
						if (!rootStep.getListeners().isEmpty())
							for (ListenerCall listener : rootStep.getListeners().get())
								addListenerCall(node, listener, new Event[] { Event.STEP_STARTED, Event.STEP_FINISHED });
					}
				}
			}
		}
		
		return retList;
	}
}
