package org.werk2.core.config.parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.werk2.config.Werk2Config;
import org.werk2.config.calls.BatchCall;
import org.werk2.config.calls.Call;
import org.werk2.config.engine.Engine;
import org.werk2.config.entities.Event;
import org.werk2.config.entities.Exec;
import org.werk2.config.entities.Flow;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.entities.Step;
import org.werk2.config.entities.StepCall;
import org.werk2.config.entities.Transit;
import org.werk2.config.functions.Function;
import org.werk2.core.config.cycles.GraphNode;

public class MermaidCallGraphBuilder {
	/*public String buildAllFunctions(Werk2Config config) {
		//TODO: implement later
		StringBuilder builder = new StringBuilder();
		
		
		
		return builder.toString();
	}*/
	
	//TODO: add ExtendedFlows
	//TODO: add ExtendedSteps
	public Engine engine;
	public Map<String, Flow> flows = new HashMap<>();
	public Map<String, Step> steps = new HashMap<>();
	public Map<String, Transit> transits = new HashMap<>();
	public Map<String, Exec> execs = new HashMap<>();
	public Map<String, Function> rawFunctions = new HashMap<>();
	
	public Map<String, GraphNode> graph = new HashMap<>();
	
	public String buildFlows(List<Werk2Config> configs) {
		StringBuilder builder = new StringBuilder();
		
		for (Werk2Config config : configs) {
			if (!config.getFlows().isEmpty())
			for (Flow flow : config.getFlows().get())
				flows.put(flow.getFunction().getFunctionName(), flow);
			
			if (!config.getSteps().isEmpty())
			for (Step step : config.getSteps().get())
				steps.put(step.getFunction().getFunctionName(), step);

			if (!config.getTransits().isEmpty())
			for (Transit transit : config.getTransits().get())
				transits.put(transit.getTransitFunctionName(), transit);

			if (!config.getExecs().isEmpty())
			for (Exec exec : config.getExecs().get())
				execs.put(exec.getExecFunctionName(), exec);

			if (!config.getRawFunctions().isEmpty())
			for (Function rawFunction : config.getRawFunctions().get())
				rawFunctions.put(rawFunction.getFunctionName(), rawFunction);

			//TODO: maybe denormalize ExtendedSteps and ExtendedFlows right here?
			/*if (!config.getExtendedFlows().isEmpty())
			for (ExtendedFlow eFlow : config.getExtendedFlows().get())
				extendedFlows.put(eFlow.getNewFunctionName(), eFlow);

			if (!config.getExtendedSteps().isEmpty())
			for (ExtendedStep eStep : config.getExtendedSteps().get())
				extendedSteps.put(eStep.getNewFunctionName(), eStep);*/
		}
		
		return builder.toString();
	}
	
	
	public void parseCall(Call c, GraphNode node, ParsingContext parsingContext) {
		node.getLinks().add(c.getFunctionName());
	}
	
	public void parseBatchCall(BatchCall batch, GraphNode node, ParsingContext parsingContext) {
		if (!batch.getCalls().isEmpty())
			for (Call c : batch.getCalls().get())
				parseCall(c, node, parsingContext);
		
		if (!batch.getBatches().isEmpty())
			for (BatchCall b : batch.getBatches().get())
				parseBatchCall(b, node, parsingContext);
	}
	
	public void parseListeners(GraphNode node, ParsingContext parsingContext, Event[] events) {
		for (ListenerCall listener  : parsingContext.filterListeners(events))
			//Listener starts in a new context
			parseBatchCall(listener, node, new ParsingContext());
	}

	public void parseFunctionAsFlow(String functionName, ParsingContext parsingContext) {
		String flowName = parsingContext.flowNamePrefix() + functionName;
		
		Flow flow = flows.get(functionName);
		
		if (graph.containsKey(flowName))
			return;
		
		parsingContext.getAncestorNames().push(flow.getFunction().getFunctionName());
		if (!flow.getListeners().isEmpty())
			parsingContext.getProjectedListeners().push(flow.getListeners().get());
		
		{
			GraphNode flowNode = new GraphNode(flowName);
			
			//TODO: own Listeners + projected listeners
			//TODO: Steps
			if (!flow.getSteps().isEmpty()) {
				for (StepCall step : flow.getSteps().get()) {
					if (!step.getStepAlias().isEmpty())
						parsingContext.getAncestorNames().push(step.getStepAlias().get());
					
					parseCall(step, flowNode, parsingContext);
					
					if (!step.getStepAlias().isEmpty())
						parsingContext.getAncestorNames().pop();
				}
			}
		}
		
		if (!flow.getListeners().isEmpty())
			parsingContext.getProjectedListeners().pop();
		parsingContext.getAncestorNames().pop();
	}
	
	public void parseStep(Step step, ParsingContext parsingContext) {
		//
	}
	
	public void parseTransit(Transit transit, ParsingContext parsingContext) {
		//
	}
	
	public void parseExec(Exec exec, ParsingContext parsingContext) {
		//
	}
	
	public void parseRawFunction(Function function, ParsingContext parsingContext) {
		//
	}
	
	public static void main(String[] args) {
		Stack<String> anc = new Stack<>();
		anc.push("Anc1");
		anc.push("Anc2");
		anc.push("Anc3");
		anc.push("Anc4");
		anc.push("Anc5");
		
		for (String a : anc) {
			System.out.println(a);
		}
	}
}
