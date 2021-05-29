package org.werk2.core.config.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.werk2.config.functions.FunctionSignature;
import org.werk2.core.config.cycles.GraphNode;
import org.werk2.core.config.prog.calls.ProgCall;
import org.werk2.core.config.prog.entities.ProgExec;
import org.werk2.core.config.prog.entities.ProgStep;
import org.werk2.core.config.prog.entities.ProgTransit;
import org.werk2.core.config.prog.functions.ProgFunction;
import org.werk2.core.config.prog.functions.ProgFunctionSignature;

//TODO: Option: try build with cycles
public class MermaidCallGraphBuilder {
	protected static String ENGINE_NAME = "Engine";
	
	//TODO: add ExtendedFlows
	//TODO: add ExtendedSteps
	public Engine engine = null;
	public Map<String, Flow> flows = new HashMap<>();
	public Map<String, Step> steps = new HashMap<>();
	public Map<String, Transit> transits = new HashMap<>();
	public Map<String, Exec> execs = new HashMap<>();
	public Map<String, Function> rawFunctions = new HashMap<>();
	
	public Map<String, GraphNode> graph = new HashMap<>();
	
	protected int anonFlowCtr = 0;
	protected int anonStepCtr = 0;
	protected int anonExecCtr = 0;
	protected int anonTransitCtr = 0;
	
	List<Werk2Config> configs;
	
	public MermaidCallGraphBuilder(List<Werk2Config> configs) {
		this.configs = configs;
		fillEntityCollections(configs);
	}
	
	protected void fillEntityCollections(List<Werk2Config> configs) {
		for (Werk2Config config : configs) {
			if (!config.getEngine().isEmpty())
			if (engine == null)
				engine = config.getEngine().get();
			
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
	}

	protected void appendLinkType(StringBuilder builder, LinkType type) {
		builder.append( String.format("%s %s %s_\n", type.legend(), type.value(), type.legend()) );
	}
	
	protected String legendString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Legend:\n"
				+ "```mermaid\n"
				+ "flowchart LR\n");		
		
		for (LinkType linkType : LinkType.values())
			appendLinkType(builder, linkType);

		builder.append("```\n");
		return builder.toString();
	}
	
	/**
	 * All functions in standalone context.
	 * All functions in direct call (Engine) context.
	 * 
	 * @return Call graph in Mermaid format
	 */
	public String buildEverything() {
		StringBuilder builder = new StringBuilder();
		builder.append(legendString())
		.append("```mermaid\n"
				+ "flowchart LR\n"
				+ "");
		
		//TODO: build
		
		builder.append("```");
		return builder.toString();
	}

	/**
	 * Flows as starting points and whatever is discoverable from there.
	 * 
	 * @return Call graph in Mermaid format
	 */
	public String buildAllFlows() {
		StringBuilder builder = new StringBuilder();
		builder.append(legendString())
		.append("```mermaid\n"
				+ "flowchart LR\n"
				+ "");

		GraphNode engineNode = new GraphNode(ENGINE_NAME);
		graph.put(ENGINE_NAME, engineNode);
		
		ParsingContext context = new ParsingContext();
		if (!engine.getListeners().isEmpty())
			context.getProjectedListeners().push(engine.getListeners().get());
		context.getAncestorNames().push(ENGINE_NAME);
		
		//TODO: add Extended flows
		for (Werk2Config config : configs) {
			if (!config.getFlows().isEmpty()) {
				for (Flow flow : config.getFlows().get()) {
					addCall(flow.getFunction().getFunctionName(), engineNode, context, 
							Optional.of(builder), LinkType.PROJECTED_CALL);
				}
			}
		}
		
		if (!engine.getListeners().isEmpty())
			context.getProjectedListeners().pop();
		context.getAncestorNames().pop();
		
		builder.append("```");
		return builder.toString();
	}

	public String buildFlow(String flowName) {
		StringBuilder builder = new StringBuilder();
		builder.append(legendString())
		.append("```mermaid\n"
				+ "flowchart LR\n"
				+ "");
		
		GraphNode engineNode = new GraphNode(ENGINE_NAME);
		graph.put(ENGINE_NAME, engineNode);
		
		ParsingContext context = new ParsingContext();
		if (!engine.getListeners().isEmpty())
			context.getProjectedListeners().push(engine.getListeners().get());
		context.getAncestorNames().push(ENGINE_NAME);
		
		addCall(flowName, engineNode, context, Optional.of(builder), LinkType.PROJECTED_CALL);
		
		if (!engine.getListeners().isEmpty())
			context.getProjectedListeners().pop();
		context.getAncestorNames().pop();
		
		builder.append("```");
		return builder.toString();
	}

	protected void addCall(String functionName, GraphNode node, ParsingContext parsingContext, 
			Optional<StringBuilder> mermaidBuilder, LinkType linkType) {
		GraphNode callNode = parseFunction(functionName, parsingContext, mermaidBuilder);
		node.getLinks().add(callNode.getName());
		
		if (!mermaidBuilder.isEmpty())
			mermaidBuilder.get().append(
				String.format("%s %s %s\n", node.getName(), linkType.value(), callNode.getName())
			);
	}
	
	protected void addCall(Call c, GraphNode node, ParsingContext parsingContext, Optional<StringBuilder> mermaidBuilder, LinkType linkType) {
		String functionName = c.getFunctionName();
		if (c instanceof StepCall) {
			StepCall stepCall = (StepCall)c;
			if (!stepCall.getStepAlias().isEmpty())
				functionName = stepCall.getStepAlias().get() + "/" + functionName; 
		}
			
		addCall(functionName, node, parsingContext, mermaidBuilder, linkType);
	}
	
	protected void addBatchCall(BatchCall batch, GraphNode node, ParsingContext parsingContext, Optional<StringBuilder> mermaidBuilder, LinkType linkType) {
		if (!batch.getCalls().isEmpty())
			for (Call c : batch.getCalls().get())
				addCall(c, node, parsingContext, mermaidBuilder, linkType);
		
		if (!batch.getBatches().isEmpty())
			for (BatchCall b : batch.getBatches().get())
				addBatchCall(b, node, parsingContext, mermaidBuilder, linkType);
	}
	
	public FunctionType getType(String functionName) {
		FunctionType functionType;
		if (flows.containsKey(functionName)) {
			functionType = FunctionType.FLOW;
		} else if (steps.containsKey(functionName)) {
			functionType = FunctionType.STEP;
		} else if (transits.containsKey(functionName)) {
			functionType = FunctionType.TRANSIT;
		} else if (execs.containsKey(functionName)) {
			functionType = FunctionType.EXEC;
		} else if (rawFunctions.containsKey(functionName)) {
			functionType = FunctionType.RAW_FUNCTION;
		} else
			throw new RuntimeException(
				String.format("Function name not found [%s]", functionName)
			);

		return functionType;
	}
	
	public GraphNode parseFunction(String functionName, ParsingContext context, Optional<StringBuilder> mermaidBuilder) {
		String fullFunctionName = context.getNamePrefix() + functionName;
		GraphNode functionNode = graph.get(fullFunctionName);
		if (functionNode != null)
			return functionNode;
		
		FunctionType functionType = getType(functionName);
		switch (functionType) {
			case FLOW : 
				functionNode = parseFlow(functionName, flows.get(functionName), context, mermaidBuilder);
				break;
			case STEP : 
				functionNode = parseStep(functionName, steps.get(functionName), context, mermaidBuilder);
				break;
			case EXEC : 
				functionNode = parseExec(functionName, execs.get(functionName), context, mermaidBuilder);
				break;
			case TRANSIT : 
				functionNode = parseTransit(functionName, transits.get(functionName), context, mermaidBuilder);
				break;
			case RAW_FUNCTION : 
				functionNode = new GraphNode(fullFunctionName);
				graph.put(fullFunctionName, functionNode);
				break;
		}
		
		return functionNode;
	}

	//--------------------------- FLOW ---------------------------
	
	protected Call anonStep(String functionName) {
		anonStepCtr++;
		String anonStepName = "#step_" + anonStepCtr;
		FunctionSignature signature = new ProgFunctionSignature(Optional.empty(), Optional.empty());
		Function anonStepFunction = new ProgFunction(Optional.empty(), anonStepName, Optional.empty(),
				Arrays.asList(new FunctionSignature[] { signature }));
		Step anonStep = new ProgStep(Optional.empty(), 
				anonStepFunction, 
				Optional.empty(),
				new ProgCall(Optional.empty(), functionName, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()),
				Optional.empty(), Optional.empty());
		
		steps.put(anonStepName, anonStep);
		
		return new ProgCall(Optional.empty(), anonStepName, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}
	
	protected Call anonTransit(String functionName) {
		anonTransitCtr++;
		String anonTransitName = "#transit_" + anonTransitCtr;
		Transit anonTransit = new ProgTransit(Optional.empty(), anonTransitName, functionName, Optional.empty(), Optional.empty());
		transits.put(anonTransitName, anonTransit);
		
		return new ProgCall(Optional.empty(), anonTransitName, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}
	
	protected Call anonExec(String functionName) {
		anonExecCtr++;
		String anonExecName = "#exec_" + anonExecCtr;
		Exec anonExec = new ProgExec(Optional.empty(), anonExecName, functionName, Optional.empty(), Optional.empty());
		execs.put(anonExecName, anonExec);
		
		return new ProgCall(Optional.empty(), anonExecName, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}

	public GraphNode parseFlow(String flowFunctionName, Flow flow, ParsingContext context, Optional<StringBuilder> mermaidBuilder) {
		if (!flow.getOverrideListeners().isEmpty() && flow.getOverrideListeners().get())
			context = new ParsingContext();
		
		String flowName = context.getNamePrefix() + flowFunctionName;
		GraphNode flowNode = graph.get(flowName);
		if (flowNode != null)
			return flowNode;
		
		flowNode = new GraphNode(flowName);
		graph.put(flowName, flowNode);
		
		//Listeners
		for (ListenerCall listener : context.filterListeners(new Event[] { Event.FLOW_STARTED, Event.FLOW_FINISHED }))
			addBatchCall(listener, flowNode, new ParsingContext(), mermaidBuilder, LinkType.PROJECTED_LISTENER);

		if (!flow.getListeners().isEmpty())
		for (ListenerCall listener : ParsingContext.filterListeners(new Event[] { Event.FLOW_STARTED, Event.FLOW_FINISHED }, flow.getListeners().get()))
			addBatchCall(listener, flowNode, new ParsingContext(), mermaidBuilder, LinkType.OWN_LISTENER);

		//Push context
		if (!flow.getListeners().isEmpty())
			context.getProjectedListeners().push(flow.getListeners().get());
		context.getAncestorNames().push(flowFunctionName);

		//Steps
		List<StepCall> allSteps = new ArrayList<>();
		allSteps.add(flow.getFirstStep());
		if (!flow.getSteps().isEmpty())
			allSteps.addAll(flow.getSteps().get());

		for (StepCall step : allSteps) {
			FunctionType functionType = getType(step.getFunctionName());
			switch (functionType) {
				case FLOW : 
					throw new RuntimeException("Flow can't be used as a Step");
				case EXEC : 
					throw new RuntimeException("Exec can't be used as a Step");
				case STEP : 
					addCall(step, flowNode, context, mermaidBuilder, LinkType.PROJECTED_CALL);
					break;
				case TRANSIT : 
					Call stepCall = anonStep(step.getFunctionName());
					addCall(stepCall, flowNode, context, mermaidBuilder, LinkType.PROJECTED_CALL);
					break;
				case RAW_FUNCTION : 
					Call transitCall = anonTransit(step.getFunctionName());
					stepCall = anonStep(transitCall.getFunctionName());
					addCall(stepCall, flowNode, context, mermaidBuilder, LinkType.PROJECTED_CALL);
					break;
			}
		}
		
		//Pop context
		if (!flow.getListeners().isEmpty())
			context.getProjectedListeners().pop();
		context.getAncestorNames().pop();
		
		return flowNode;
	}

	//--------------------------- STEP ---------------------------
	
	protected void addStepExecCall(Call c, GraphNode node, ParsingContext context, Optional<StringBuilder> mermaidBuilder) {
		FunctionType functionType = getType(c.getFunctionName());
		switch (functionType) {
			case EXEC : 
				addCall(c, node, context, mermaidBuilder, LinkType.PROJECTED_CALL);
				break;
			case FLOW : 
			case STEP : 
			case TRANSIT : 
			case RAW_FUNCTION : 
				Call execCall = anonExec(c.getFunctionName());
				addCall(execCall, node, context, mermaidBuilder, LinkType.PROJECTED_CALL);
				break;
		}
	}

	protected void addStepTransitCall(Call c, GraphNode node, ParsingContext context, Optional<StringBuilder> mermaidBuilder) {
		FunctionType functionType = getType(c.getFunctionName());
		switch (functionType) {
			case FLOW : 
				throw new RuntimeException("Flow can't be used as a Transit");
			case EXEC : 
				throw new RuntimeException("Exec can't be used as a Transit");
				
			case TRANSIT : 
				addCall(c, node, context, mermaidBuilder, LinkType.PROJECTED_CALL);
				break;
			case STEP : 
			case RAW_FUNCTION : 
				Call transitCall = anonTransit(c.getFunctionName());
				addCall(transitCall, node, context, mermaidBuilder, LinkType.PROJECTED_CALL);
				break;
		}
	}

	protected void addStepExecBlock(BatchCall batch, GraphNode node, ParsingContext parsingContext, Optional<StringBuilder> mermaidBuilder) {
		if (!batch.getCalls().isEmpty())
			for (Call c : batch.getCalls().get())
				addStepExecCall(c, node, parsingContext, mermaidBuilder);
		
		if (!batch.getBatches().isEmpty())
			for (BatchCall b : batch.getBatches().get())
				addStepExecBlock(b, node, parsingContext, mermaidBuilder);
	}

	public GraphNode parseStep(String stepFunctionName, Step step, ParsingContext context, Optional<StringBuilder> mermaidBuilder) {
		if (!step.getOverrideListeners().isEmpty() && step.getOverrideListeners().get())
			context = new ParsingContext();
		
		String stepName = context.getNamePrefix() + stepFunctionName;
		GraphNode stepNode = graph.get(stepName);
		if (stepNode != null)
			return stepNode;
		
		stepNode = new GraphNode(stepName);
		graph.put(stepName, stepNode);
		
		//Listeners
		for (ListenerCall listener : context.filterListeners(
				new Event[] { Event.STEP_STARTED, Event.STEP_FINISHED, 
								Event.STEP_ITER_STARTED, Event.STEP_ITER_FINISHED })) {
			addBatchCall(listener, stepNode, new ParsingContext(), mermaidBuilder, LinkType.PROJECTED_LISTENER);
		}

		if (!step.getListeners().isEmpty())
		for (ListenerCall listener : ParsingContext.filterListeners(new Event[] { Event.STEP_STARTED, Event.STEP_FINISHED, 
				Event.STEP_ITER_STARTED, Event.STEP_ITER_FINISHED }, step.getListeners().get()))
			addBatchCall(listener, stepNode, new ParsingContext(), mermaidBuilder, LinkType.OWN_LISTENER);
		
		//Push context
		if (!step.getListeners().isEmpty())
			context.getProjectedListeners().push(step.getListeners().get());
		context.getAncestorNames().push(stepFunctionName);

		//Executors
		if (!step.getExecBlock().isEmpty())
			addStepExecBlock(step.getExecBlock().get(), stepNode, context, mermaidBuilder);

		//Transit
		addStepTransitCall(step.getTransit(), stepNode, context, mermaidBuilder);
		
		//Pop context
		if (!step.getListeners().isEmpty())
			context.getProjectedListeners().pop();
		context.getAncestorNames().pop();
		
		return stepNode;
	}
	
	//--------------------------- EXEC ---------------------------
	
	public GraphNode parseExec(String execFunctionName, Exec exec, ParsingContext context, Optional<StringBuilder> mermaidBuilder) {
		if (!exec.getOverrideListeners().isEmpty() && exec.getOverrideListeners().get())
			context = new ParsingContext();
		
		String execName = context.getNamePrefix() + execFunctionName;
		GraphNode execNode = graph.get(execName);
		if (execNode != null)
			return execNode;
		
		execNode = new GraphNode(execName);
		graph.put(execName, execNode);
		
		//Listeners
		for (ListenerCall listener : context.filterListeners(
				new Event[] { Event.EXECUTOR_STARTED, Event.EXECUTOR_FINISHED })) {
			addBatchCall(listener, execNode, new ParsingContext(), mermaidBuilder, LinkType.PROJECTED_LISTENER);
		}

		if (!exec.getListeners().isEmpty())
		for (ListenerCall listener : ParsingContext.filterListeners(new Event[] { Event.EXECUTOR_STARTED, Event.EXECUTOR_FINISHED }, exec.getListeners().get()))
			addBatchCall(listener, execNode, new ParsingContext(), mermaidBuilder, LinkType.OWN_LISTENER);
		
		//Push context
		if (!exec.getListeners().isEmpty())
			context.getProjectedListeners().push(exec.getListeners().get());
		context.getAncestorNames().push(execFunctionName);

		//Called function
		addCall(exec.getCalledFunctionName(), execNode, new ParsingContext(), mermaidBuilder, LinkType.NEW_CONTEXT_CALL);
		
		//Pop context
		if (!exec.getListeners().isEmpty())
			context.getProjectedListeners().pop();
		context.getAncestorNames().pop();
		
		return execNode;
	}
	
	//--------------------------- TRANSIT ---------------------------
	
	public GraphNode parseTransit(String transitFunctionName, Transit transit, ParsingContext context, Optional<StringBuilder> mermaidBuilder) {
		if (!transit.getOverrideListeners().isEmpty() && transit.getOverrideListeners().get())
			context = new ParsingContext();
		
		String transitName = context.getNamePrefix() + transitFunctionName;
		GraphNode transitNode = graph.get(transitName);
		if (transitNode != null)
			return transitNode;
		
		transitNode = new GraphNode(transitName);
		graph.put(transitName, transitNode);
		
		//Listeners
		for (ListenerCall listener : context.filterListeners(
				new Event[] { Event.TRANSITIONER_STARTED, Event.TRANSITIONER_FINISHED })) {
			addBatchCall(listener, transitNode, new ParsingContext(), mermaidBuilder, LinkType.PROJECTED_LISTENER);
		}

		if (!transit.getListeners().isEmpty())
		for (ListenerCall listener : ParsingContext.filterListeners(new Event[] { Event.TRANSITIONER_STARTED, Event.TRANSITIONER_FINISHED }, transit.getListeners().get()))
			addBatchCall(listener, transitNode, new ParsingContext(), mermaidBuilder, LinkType.OWN_LISTENER);
		
		//Push context
		if (!transit.getListeners().isEmpty())
			context.getProjectedListeners().push(transit.getListeners().get());
		context.getAncestorNames().push(transitFunctionName);

		//Called function
		addCall(transit.getCalledFunctionName(), transitNode, new ParsingContext(), mermaidBuilder, LinkType.NEW_CONTEXT_CALL);
		
		//Pop context
		if (!transit.getListeners().isEmpty())
			context.getProjectedListeners().pop();
		context.getAncestorNames().pop();
		
		return transitNode;
	}
}
