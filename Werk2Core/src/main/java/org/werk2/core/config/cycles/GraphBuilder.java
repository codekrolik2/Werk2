package org.werk2.core.config.cycles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.werk2.common.WerkConfigException;
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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class GraphBuilder {
	/**
	 * Denormalized Node 
	 * @author jamirov 
	 */
	@RequiredArgsConstructor @Getter
	class DNNode {
		@NonNull protected String functionName;
		/** Entity's own and inherited links. No ancestor-projected listeners or listeners for descendant events. */
		protected List<String> links = new ArrayList<>();
		/** Entity's own and inherited listeners. No ancestor-projected listeners. */
		protected List<ListenerCall> allListeners = new ArrayList<>();

		public GraphNode cloneToGraphNode() {
			GraphNode node = new GraphNode(functionName);
			node.setLinks(new ArrayList<>(links));
			return node;
		}
	}
	
	//TODO: initial structure is defined by original traversal - includes inheritance decoupling
	//TODO: In the final version different versions of the same step/flow may emerge due to listener projection
	//			i.e. with different sets of listeners
	
	//TODO: Tree structure for listeners: Engine-defined Listeners, Flow-defined Listeners, Step-defined Listeners
	//		should be applied to all sub-Entities, unless overridden
	
	//TODO:
	//Raw functions don't call listeners
	//Listener projection: Engine -> Flow -> Step -> Transit
	//                                          \-> ExecBlock -> Exec
	
	//TODO: apply listener projection
	
	
	public List<GraphNode> buildGraph(List<Werk2Config> configs, Map<String, DNNode> denormalizedNodes) throws WerkConfigException {
		List<GraphNode> retList = new ArrayList<>();
		
		//Find engine
		Engine engine = null;
		for (Werk2Config config : configs) {
			if (!config.getEngine().isEmpty()) {
				if (engine != null)
					throw new WerkConfigException(
						String.format("Only one engine is permitted [%s] [%s]", 
							config.getEngine().get().toString(), engine.toString())
					);
			}
		}
		
		//regular - functionName
		//with projection - projFun1->projFun2->projFun3-> ... ->functionName
		
		//Add all nodes as first-class citizens (direct calls)
		for (DNNode node : denormalizedNodes.values())
			retList.add(node.cloneToGraphNode());

		
		return retList;
	}
	
	protected void projectFlow() {
		//
	}
	
	protected void projectStep() {
		//
	}
	
	protected void projectExec() {
		//
	}
	
	protected void projectTransit() {
		//
	}
	
	//-------------------------------------------------------------------

	// TODO: ensure correctness
	protected void addFunction(DNNode node, String functionName) {
		node.getLinks().add(node.getFunctionName() + "->" + functionName);
	}
	
	protected void addCall(DNNode node, Call call) {
		addFunction(node, call.getFunctionName());
	}
	
	protected void addBatchCall(DNNode node, BatchCall batchCall) {
		if (!batchCall.getCalls().isEmpty())
		for (Call call : batchCall.getCalls().get())
			addCall(node, call);
		
		if (!batchCall.getBatches().isEmpty())
		for (BatchCall batch : batchCall.getBatches().get())
			addBatchCall(node, batch);
	}
	
	protected void addListenerCalls(DNNode node, List<? extends ListenerCall> listenerCalls, Event[] events) {
		Set<Event> eventsSet = new HashSet<>();
		for (Event event : events)
			eventsSet.add(event);

		for (ListenerCall listenerCall : listenerCalls) {
			for (Event event : listenerCall.getEvents()) {
				if (eventsSet.contains(event)) {
					addBatchCall(node, listenerCall);
					break;
				}
			}
		}
		
		node.getAllListeners().addAll(listenerCalls);
	}

	/**
	 * Traverse Werk configurations, identify functions,
	 * denormalize inheritance structures for ExtendedSteps and ExtendedFlows,
	 * represent all functions in their denormalized form in a map indexed by their names.
	 * 
	 * @param configs Werk configuration profiles
	 * @return Map of function names to denormalized nodes.
	 */
	protected Map<String, DNNode> getAllDNNodes(List<Werk2Config> configs) {
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
		
		Map<String, DNNode> dnList = new HashMap<>();
		
		for (Werk2Config config : configs) {
			//Raw Functions
			if (!config.getRawFunctions().isEmpty())
			for (Function rawFunction : config.getRawFunctions().get()) {
				DNNode node = new DNNode(rawFunction.getFunctionName());
				dnList.put(node.getFunctionName(), node);
			}

			//Exec
			if (!config.getExecs().isEmpty())
			for (Exec exec : config.getExecs().get()) {
				DNNode node = new DNNode(exec.getExecFunctionName());
				dnList.put(node.getFunctionName(), node);
				
				addFunction(node, exec.getCalledFunctionName());

				if (!exec.getListeners().isEmpty())
					addListenerCalls(node, exec.getListeners().get(), 
							new Event[] { Event.EXECUTOR_STARTED, Event.EXECUTOR_FINISHED });
			}

			//Transit
			if (!config.getTransits().isEmpty())
			for (Transit transit : config.getTransits().get()) {
				DNNode node = new DNNode(transit.getTransitFunctionName());
				dnList.put(node.getFunctionName(), node);
				
				addFunction(node, transit.getCalledFunctionName());

				if (!transit.getListeners().isEmpty())
					addListenerCalls(node, transit.getListeners().get(), 
							new Event[] { Event.TRANSITIONER_STARTED, Event.TRANSITIONER_FINISHED });
			}

			//Step
			if (!config.getSteps().isEmpty())
			for (Step step : config.getSteps().get()) {
				DNNode node = new DNNode(step.getFunction().getFunctionName());
				dnList.put(node.getFunctionName(), node);
				
				addCall(node, step.getTransit());

				if (!step.getExecBlock().isEmpty())
					addBatchCall(node, step.getExecBlock().get());

				if (!step.getListeners().isEmpty())
					addListenerCalls(node, step.getListeners().get(), 
							new Event[] { Event.STEP_STARTED, Event.STEP_FINISHED, 
									Event.STEP_ITER_STARTED, Event.STEP_ITER_FINISHED });
			}

			//Flows
			if (!config.getFlows().isEmpty())
			for (Flow flow : config.getFlows().get()) {
				DNNode node = new DNNode(flow.getFunction().getFunctionName());
				dnList.put(node.getFunctionName(), node);
				
				addCall(node, flow.getFirstStep());

				if (!flow.getSteps().isEmpty())
					for (StepCall step : flow.getSteps().get())
						addCall(node, step);

				if (!flow.getListeners().isEmpty())
					addListenerCalls(node, flow.getListeners().get(), 
							new Event[] { Event.FLOW_STARTED, Event.FLOW_FINISHED });
			}
			
			//Extended Flow
			//FlowInheritance: Steps and Listeners from Super-Flow declarations
			//should be applied to ExtendedFlows recursively, unless overridden.
			if (!config.getExtendedFlows().isEmpty())
			for (ExtendedFlow eFlow : config.getExtendedFlows().get()) {
				DNNode node = new DNNode(eFlow.getNewFunctionName());
				dnList.put(node.getFunctionName(), node);

				//1. First step
				if (!eFlow.getNewFirstStep().isEmpty()) {
					//If the first step is redefined here - use it
					addCall(node, eFlow.getNewFirstStep().get());
				} else {
					//otherwise find the first redefinition in ancestral ExtendedFlows
					ExtendedFlow current = eFlow;
					boolean firstStepAssigned = false;
					while (extendedFlows.containsKey(current.getSuperFlowFunctionName())) {
						current = extendedFlows.get(current.getSuperFlowFunctionName());
						if (!current.getNewFirstStep().isEmpty()) {
							addCall(node, current.getNewFirstStep().get());
							firstStepAssigned = true;
							break;
						}
					}
					//if no ancestral ExtendedFlow redefines the FirstStep - get the original FirstStep
					if (!firstStepAssigned) {
						Flow rootFlow = flows.get(current.getSuperFlowFunctionName());
						if (!firstStepAssigned)
							addCall(node, rootFlow.getFirstStep());
					}
				}

				//2. Steps
				//Add all steps registered on extended flow
				if (!eFlow.getAddSteps().isEmpty())
					for (StepCall step : eFlow.getAddSteps().get())
						addCall(node, step);

				//If not explicitly overridden, add steps from extended super-flow declarations (recursive)
				if (eFlow.getDropOldSteps().isEmpty() || !eFlow.getDropOldSteps().get()) {
					ExtendedFlow current = eFlow;
					boolean drop = false;
					while (extendedFlows.containsKey(current.getSuperFlowFunctionName())) {
						current = extendedFlows.get(current.getSuperFlowFunctionName());
						if (!current.getAddSteps().isEmpty())
							for (StepCall step : current.getAddSteps().get())
								addCall(node, step);
						
						//Check if the current ancestor overrides steps from its super-flows
						if (!current.getDropOldSteps().isEmpty() && current.getDropOldSteps().get()) {
							drop = true;
							break;
						}
					}
					
					//If not overridden so far, also add Steps from root Flow declaration
					if (!drop) {
						Flow rootFlow = flows.get(current.getSuperFlowFunctionName());
						if (!rootFlow.getSteps().isEmpty())
							for (StepCall step : rootFlow.getSteps().get())
								addCall(node, step);
					}
				}
				
				//3. Add listeners for flow events (FLOW_STARTED, FLOW_FINISHED)
				if (!eFlow.getAddListeners().isEmpty())
					addListenerCalls(node, eFlow.getAddListeners().get(), 
							new Event[] { Event.FLOW_STARTED, Event.FLOW_FINISHED });

				//If not explicitly overridden, add Listeners from extended super-flow declarations (recursive)
				if (eFlow.getDropOldListeners().isEmpty() || !eFlow.getDropOldListeners().get()) {
					ExtendedFlow current = eFlow;
					boolean drop = false;
					while (extendedFlows.containsKey(current.getSuperFlowFunctionName())) {
						current = extendedFlows.get(current.getSuperFlowFunctionName());
						if (!current.getAddListeners().isEmpty())
							addListenerCalls(node, current.getAddListeners().get(), 
									new Event[] { Event.FLOW_STARTED, Event.FLOW_FINISHED });
						
						//Check if the current ancestor overrides Listeners from its super-flows
						if (!current.getDropOldListeners().isEmpty() && current.getDropOldListeners().get()) {
							drop = true;
							break;
						}
					}

					//If not overridden so far, also add Listeners from root Flow declaration
					if (!drop) {
						Flow rootFlow = flows.get(current.getSuperFlowFunctionName());
						if (!rootFlow.getListeners().isEmpty())
							addListenerCalls(node, rootFlow.getListeners().get(), 
									new Event[] { Event.FLOW_STARTED, Event.FLOW_FINISHED });
					}
				}
			}

			//Extended Step
			//Step Inheritance: Execs, Transit, Listeners from super-Step declarations
			//should be applied to ExtendedSteps recursively, unless overridden
			if (!config.getExtendedSteps().isEmpty())
			for (ExtendedStep eStep : config.getExtendedSteps().get()) {
				DNNode node = new DNNode(eStep.getNewFunctionName());
				dnList.put(node.getFunctionName(), node);

				//1. Add transit, if overridden
				if (!eStep.getNewTransit().isEmpty()) {
					addCall(node, eStep.getNewTransit().get());
				} else {
					//Otherwise try to find an override on ancestors ExtendedSteps
					ExtendedStep current = eStep;
					boolean transitAssigned = false;
					while (extendedSteps.containsKey(current.getSuperStepFunctionName())) {
						current = extendedSteps.get(current.getSuperStepFunctionName());
						if (!current.getNewTransit().isEmpty()) {
							addCall(node, current.getNewTransit().get());
							transitAssigned = true;
							break;
						}
					}
					//If not overridden, add transit from original root Step
					if (!transitAssigned) {
						Step rootStep = steps.get(current.getSuperStepFunctionName());
						addCall(node, rootStep.getTransit());
					}
				}
				
				//2. Add exec block
				if (!eStep.getAddExecBlock().isEmpty())
					addBatchCall(node, eStep.getAddExecBlock().get());

				//If not explicitly overridden, add execs from extended super-step declarations (recursive)
				if (eStep.getDropOldExecBlock().isEmpty() || !eStep.getDropOldExecBlock().get()) {
					ExtendedStep current = eStep;
					boolean drop = false;
					while (extendedSteps.containsKey(current.getSuperStepFunctionName())) {
						current = extendedSteps.get(current.getSuperStepFunctionName());
						if (!current.getAddExecBlock().isEmpty())
							addBatchCall(node, current.getAddExecBlock().get());
						
						//Check if the current ancestor overrides execs from its super-Steps
						if (!current.getDropOldExecBlock().isEmpty() && current.getDropOldExecBlock().get()) {
							drop = true;
							break;
						}
					}
					
					//If not overridden so far, also add Execs from root Step declaration
					if (!drop) {
						Step rootStep = steps.get(current.getSuperStepFunctionName());
						if (!rootStep.getExecBlock().isEmpty())
							addBatchCall(node, rootStep.getExecBlock().get());
					}
				}

				//3. Add listeners for step events (STEP_STARTED, STEP_FINISHED)
				if (!eStep.getAddListeners().isEmpty())
					addListenerCalls(node, eStep.getAddListeners().get(), 
							new Event[] { Event.STEP_STARTED, Event.STEP_FINISHED, Event.STEP_ITER_STARTED, Event.STEP_ITER_FINISHED });
				
				//If not explicitly overridden, add Listeners from extended super-Step declarations (recursive)
				if (eStep.getDropOldListeners().isEmpty() || !eStep.getDropOldListeners().get()) {
					ExtendedStep current = eStep;
					boolean drop = false;
					while (extendedSteps.containsKey(current.getSuperStepFunctionName())) {
						current = extendedSteps.get(current.getSuperStepFunctionName());
						if (!current.getAddListeners().isEmpty())
							addListenerCalls(node, current.getAddListeners().get(), 
									new Event[] { Event.STEP_STARTED, Event.STEP_FINISHED, 
											Event.STEP_ITER_STARTED, Event.STEP_ITER_FINISHED });
						
						//Check if the current ancestor overrides Listeners from its super-Steps
						if (!current.getDropOldListeners().isEmpty() && current.getDropOldListeners().get()) {
							drop = true;
							break;
						}
					}
					
					//If not overridden so far, also add Listeners from root Step declaration
					if (!drop) {
						Step rootStep = steps.get(current.getSuperStepFunctionName());
						if (!rootStep.getListeners().isEmpty())
							addListenerCalls(node, rootStep.getListeners().get(), 
									new Event[] { Event.STEP_STARTED, Event.STEP_FINISHED, 
											Event.STEP_ITER_STARTED, Event.STEP_ITER_FINISHED });
					}
				}
			}
		}
		
		return dnList;
	}
}
