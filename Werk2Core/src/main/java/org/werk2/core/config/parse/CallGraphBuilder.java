package org.werk2.core.config.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.werk2.config.Werk2Config;
import org.werk2.config.calls.BatchCall;
import org.werk2.config.calls.Call;
import org.werk2.config.entities.Event;
import org.werk2.config.entities.Exec;
import org.werk2.config.entities.Flow;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.entities.Step;
import org.werk2.config.entities.StepCall;
import org.werk2.config.entities.Transit;
import org.werk2.core.config.cycles.GraphNode;

/**
 * Builds full call graph from configuration(s)
 * @author jamirov
 *
 */
public class CallGraphBuilder extends GraphBuilderBase {
	protected Map<String, GraphNode> initGraph = new HashMap<>();

	public CallGraphBuilder(List<Werk2Config> configs) {
		super(configs, true);
		
		GraphNode engineNode = new GraphNode(ENGINE_NAME);
		initGraph.put(ENGINE_NAME, engineNode);
	}

	//--------------------------- CALL GRAPH BUILDERS ---------------------------

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

		builder.append("```\n---\n");
		return builder.toString();
	}
	
	/**
	 * All functions in direct call (Engine) context.
	 * 
	 * @return Call graph in Mermaid format
	 */
	public Map<String, GraphNode> buildEverything(Optional<StringBuilder> builder) {
		if (!builder.isEmpty())
			builder.get().append(legendString())
			.append("```mermaid\n"
					+ "flowchart LR\n");

		ParsingContext context = new ParsingContext();
		if (!engine.getListeners().isEmpty())
			context.getProjectedListeners().push(engine.getListeners().get());
		context.getAncestorNames().push(ENGINE_NAME);
		
		Map<String, GraphNode> graph = new HashMap<>(initGraph);
		GraphNode engineNode = graph.get(ENGINE_NAME);
		
		for (String functionName : flows.keySet())
			parseDirectFunctionCall(functionName, engineNode, context, builder, graph);
		for (String functionName : steps.keySet())
			parseDirectFunctionCall(functionName, engineNode, context, builder, graph);
		for (String functionName : transits.keySet())
			parseDirectFunctionCall(functionName, engineNode, context, builder, graph);
		for (String functionName : execs.keySet())
			parseDirectFunctionCall(functionName, engineNode, context, builder, graph);
		for (String functionName : rawExecFunctions.keySet())
			parseDirectFunctionCall(functionName, engineNode, context, builder, graph);
		for (String functionName : rawTransitFunctions.keySet())
			parseDirectFunctionCall(functionName, engineNode, context, builder, graph);
		
		if (!engine.getListeners().isEmpty())
			context.getProjectedListeners().pop();
		context.getAncestorNames().pop();
		
		if (!builder.isEmpty())
			builder.get().append("```");
		return graph;
	}

	/**
	 * Flows as starting points and whatever is discoverable from there.
	 * 
	 * @return Call graph in Mermaid format
	 */
	public Map<String, GraphNode> buildAllFlows(Optional<StringBuilder> builder) {
		if (!builder.isEmpty())
			builder.get().append(legendString())
			.append("```mermaid\n"
					+ "flowchart LR\n");

		ParsingContext context = new ParsingContext();
		if (!engine.getListeners().isEmpty())
			context.getProjectedListeners().push(engine.getListeners().get());
		context.getAncestorNames().push(ENGINE_NAME);
		
		Map<String, GraphNode> graph = new HashMap<>(initGraph);
		GraphNode engineNode = graph.get(ENGINE_NAME);
		for (Werk2Config config : configs) {
			if (!config.getFlows().isEmpty()) {
				for (Flow flow : config.getFlows().get()) {
					addCall(flow.getFunction().getFunctionName(), engineNode, context, 
							builder, LinkType.PROJECTED_CALL, graph);
				}
			}
		}
		
		if (!engine.getListeners().isEmpty())
			context.getProjectedListeners().pop();
		context.getAncestorNames().pop();
		
		if (!builder.isEmpty())
			builder.get().append("```");
		return graph;
	}

	/**
	 * Any function as a starting point and whatever is discoverable from there.
	 * 
	 * @param functionName Entry point function
	 * @return Call graph in Mermaid format
	 */
	public Map<String, GraphNode> buildFunction(String functionName, Optional<StringBuilder> builder) {
		if (!builder.isEmpty())
			builder.get().append(legendString())
			.append("```mermaid\n"
					+ "flowchart LR\n");
		
		ParsingContext context = new ParsingContext();
		if (!engine.getListeners().isEmpty())
			context.getProjectedListeners().push(engine.getListeners().get());
		context.getAncestorNames().push(ENGINE_NAME);
		
		Map<String, GraphNode> graph = new HashMap<>(initGraph);
		GraphNode engineNode = graph.get(ENGINE_NAME);
		
		parseDirectFunctionCall(functionName, engineNode, context, builder, graph);
		
		if (!engine.getListeners().isEmpty())
			context.getProjectedListeners().pop();
		context.getAncestorNames().pop();
		
		if (!builder.isEmpty())
			builder.get().append("```");
		return graph;
	}

	/**
	 * Any function as a starting point (without Engine context) and whatever is discoverable from there.
	 * 
	 * @param functionName Entry point function
	 * @return Call graph in Mermaid format
	 */
	public Map<String, GraphNode> buildStandaloneFunction(String functionName, Optional<StringBuilder> builder) {
		if (!builder.isEmpty())
			builder.get().append(legendString())
			.append("```mermaid\n"
					+ "flowchart LR\n");
		
		Map<String, GraphNode> graph = new HashMap<>(initGraph);

		ParsingContext context = new ParsingContext();
		addCall(functionName, null, context, builder, LinkType.PROJECTED_CALL, graph);
		
		if (!builder.isEmpty())
			builder.get().append("```");
		return graph;
	}

	//--------------------------- ADD CALL TO GRAPH ---------------------------

	protected void addCall(String functionName, GraphNode node, ParsingContext parsingContext, 
			Optional<StringBuilder> mermaidBuilder, LinkType linkType, Map<String, GraphNode> graph) {
		GraphNode callNode = parseFunction(functionName, parsingContext, mermaidBuilder, graph);
		if (node != null) {
			node.getLinks().add(callNode.getName());
		
			if (!mermaidBuilder.isEmpty())
				mermaidBuilder.get().append(
					String.format("%s %s %s\n", node.getName(), linkType.value(), callNode.getName())
				);
		} else {
			if (!mermaidBuilder.isEmpty())
				mermaidBuilder.get().append(
					String.format("%s\n", functionName)
				);
		}
	}
	
	protected void addCall(Call c, GraphNode node, ParsingContext parsingContext, Optional<StringBuilder> mermaidBuilder, LinkType linkType, Map<String, GraphNode> graph) {
		String functionName = c.getFunctionName();
		if (c instanceof StepCall) {
			StepCall stepCall = (StepCall)c;
			if (!stepCall.getStepAlias().isEmpty())
				functionName = stepCall.getStepAlias().get() + "/" + functionName;
		}
		
		addCall(functionName, node, parsingContext, mermaidBuilder, linkType, graph);
	}
	
	protected void addBatchCall(BatchCall batch, GraphNode node, ParsingContext parsingContext, Optional<StringBuilder> mermaidBuilder, LinkType linkType, Map<String, GraphNode> graph) {
		if (!batch.getCalls().isEmpty())
			for (Call c : batch.getCalls().get())
				addCall(c, node, parsingContext, mermaidBuilder, linkType, graph);
		
		if (!batch.getBatches().isEmpty())
			for (BatchCall b : batch.getBatches().get())
				addBatchCall(b, node, parsingContext, mermaidBuilder, linkType, graph);
	}
	
	protected void parseDirectFunctionCall(String functionName, GraphNode engineNode, ParsingContext engineContext, 
			Optional<StringBuilder> mermaidBuilder, Map<String, GraphNode> graph) {
		FunctionType functionType = getType(functionName);
		switch (functionType) {
			case FLOW : 
				addCall(functionName, engineNode, engineContext, mermaidBuilder, LinkType.PROJECTED_CALL, graph);
				break;
			case STEP : {
				Call flowCall = anonFlow(functionName);
				addCall(flowCall, engineNode, engineContext, mermaidBuilder, LinkType.PROJECTED_CALL, graph);
				break;
			}
			case EXEC : {
				Call stepCall = anonStepForExecFunction(functionName);
				Call flowCall = anonFlow(stepCall.getFunctionName());
				addCall(flowCall, engineNode, engineContext, mermaidBuilder, LinkType.PROJECTED_CALL, graph);
				break;
			}
			case TRANSIT : {
				Call stepCall = anonStepForTransitFunction(functionName);
				Call flowCall = anonFlow(stepCall.getFunctionName());
				addCall(flowCall, engineNode, engineContext, mermaidBuilder, LinkType.PROJECTED_CALL, graph);
				break;
			}
			case RAW_EXEC_FUNCTION : {
				Call execCall = anonExec(functionName);
				Call stepCall = anonStepForExecFunction(execCall.getFunctionName());
				Call flowCall = anonFlow(stepCall.getFunctionName());
				addCall(flowCall, engineNode, engineContext, mermaidBuilder, LinkType.PROJECTED_CALL, graph);
				break;
			}
			//case RAW_TRANSIT_FUNCTION :
			default : {
				Call transitCall = anonTransit(functionName);
				Call stepCall = anonStepForTransitFunction(transitCall.getFunctionName());
				Call flowCall = anonFlow(stepCall.getFunctionName());
				addCall(flowCall, engineNode, engineContext, mermaidBuilder, LinkType.PROJECTED_CALL, graph);
				break;
			}
		}
	}

	//--------------------------- GENERIC FUNCTION ---------------------------
	
	protected GraphNode parseFunction(String functionName, ParsingContext context, Optional<StringBuilder> mermaidBuilder, Map<String, GraphNode> graph) {
		String fullFunctionName = context.getNamePrefix() + functionName;
		GraphNode functionNode = graph.get(fullFunctionName);
		if (functionNode != null)
			return functionNode;
		
		FunctionType functionType = getType(functionName);
		switch (functionType) {
			case FLOW : 
				functionNode = parseFlow(functionName, flows.get(functionName), context, mermaidBuilder, graph);
				break;
			case STEP : 
				functionNode = parseStep(functionName, steps.get(functionName), context, mermaidBuilder, graph);
				break;
			case EXEC : 
				functionNode = parseExec(functionName, execs.get(functionName), context, mermaidBuilder, graph);
				break;
			case TRANSIT : 
				functionNode = parseTransit(functionName, transits.get(functionName), context, mermaidBuilder, graph);
				break;
			case RAW_EXEC_FUNCTION : 
			case RAW_TRANSIT_FUNCTION : 
				functionNode = new GraphNode(fullFunctionName);
				graph.put(fullFunctionName, functionNode);
				break;
		}
		
		return functionNode;
	}

	//--------------------------- FLOW ---------------------------

	protected GraphNode parseFlow(String flowFunctionName, Flow flow, ParsingContext context, Optional<StringBuilder> mermaidBuilder, Map<String, GraphNode> graph) {
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
			addBatchCall(listener, flowNode, new ParsingContext(), mermaidBuilder, LinkType.PROJECTED_LISTENER, graph);

		if (!flow.getListeners().isEmpty())
		for (ListenerCall listener : ParsingContext.filterListeners(new Event[] { Event.FLOW_STARTED, Event.FLOW_FINISHED }, flow.getListeners().get()))
			addBatchCall(listener, flowNode, new ParsingContext(), mermaidBuilder, LinkType.OWN_LISTENER, graph);

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
				case RAW_EXEC_FUNCTION : 
					throw new RuntimeException("Raw Exec Function can't be used as a Step");
				case STEP : 
					addCall(step, flowNode, context, mermaidBuilder, LinkType.PROJECTED_CALL, graph);
					break;
				case TRANSIT : {
					Call stepCall = anonStepForTransitFunction(step.getFunctionName());
					addCall(stepCall, flowNode, context, mermaidBuilder, LinkType.PROJECTED_CALL, graph);
					break;
				}
				case RAW_TRANSIT_FUNCTION : {
					Call transitCall = anonTransit(step.getFunctionName());
					Call stepCall = anonStepForTransitFunction(transitCall.getFunctionName());
					addCall(stepCall, flowNode, context, mermaidBuilder, LinkType.PROJECTED_CALL, graph);
					break;
				}
			}
		}
		
		//Pop context
		if (!flow.getListeners().isEmpty())
			context.getProjectedListeners().pop();
		context.getAncestorNames().pop();
		
		return flowNode;
	}

	//--------------------------- STEP ---------------------------
	
	protected void addStepTransitCall(Call c, GraphNode node, ParsingContext context, Optional<StringBuilder> mermaidBuilder, Map<String, GraphNode> graph) {
		FunctionType functionType = getType(c.getFunctionName());
		switch (functionType) {
			case FLOW : 
				throw new RuntimeException("Flow can't be used as a Transit");
			case EXEC : 
				throw new RuntimeException("Exec can't be used as a Transit");
			case RAW_EXEC_FUNCTION : 
				throw new RuntimeException("Raw Exec function can't be used as a Transit");
				
			case TRANSIT : 
				addCall(c, node, context, mermaidBuilder, LinkType.PROJECTED_CALL, graph);
				break;
			case STEP : 
			case RAW_TRANSIT_FUNCTION : 
				Call transitCall = anonTransit(c.getFunctionName());
				addCall(transitCall, node, context, mermaidBuilder, LinkType.PROJECTED_CALL, graph);
				break;
		}
	}

	protected void addStepExecCall(Call c, GraphNode node, ParsingContext context, Optional<StringBuilder> mermaidBuilder, Map<String, GraphNode> graph) {
		FunctionType functionType = getType(c.getFunctionName());
		switch (functionType) {
			case EXEC : 
				addCall(c, node, context, mermaidBuilder, LinkType.PROJECTED_CALL, graph);
				break;
			case FLOW : 
			case STEP : 
			case TRANSIT : 
			case RAW_EXEC_FUNCTION : 
			case RAW_TRANSIT_FUNCTION : 
				Call execCall = anonExec(c.getFunctionName());
				addCall(execCall, node, context, mermaidBuilder, LinkType.PROJECTED_CALL, graph);
				break;
		}
	}

	protected void addStepExecBlock(BatchCall batch, GraphNode node, ParsingContext parsingContext, Optional<StringBuilder> mermaidBuilder, Map<String, GraphNode> graph) {
		if (!batch.getCalls().isEmpty())
			for (Call c : batch.getCalls().get())
				addStepExecCall(c, node, parsingContext, mermaidBuilder, graph);
		
		if (!batch.getBatches().isEmpty())
			for (BatchCall b : batch.getBatches().get())
				addStepExecBlock(b, node, parsingContext, mermaidBuilder, graph);
	}

	protected GraphNode parseStep(String stepFunctionName, Step step, ParsingContext context, Optional<StringBuilder> mermaidBuilder, Map<String, GraphNode> graph) {
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
			addBatchCall(listener, stepNode, new ParsingContext(), mermaidBuilder, LinkType.PROJECTED_LISTENER, graph);
		}

		if (!step.getListeners().isEmpty())
		for (ListenerCall listener : ParsingContext.filterListeners(new Event[] { Event.STEP_STARTED, Event.STEP_FINISHED, 
				Event.STEP_ITER_STARTED, Event.STEP_ITER_FINISHED }, step.getListeners().get()))
			addBatchCall(listener, stepNode, new ParsingContext(), mermaidBuilder, LinkType.OWN_LISTENER, graph);
		
		//Push context
		if (!step.getListeners().isEmpty())
			context.getProjectedListeners().push(step.getListeners().get());
		context.getAncestorNames().push(stepFunctionName);

		//Executors
		if (!step.getExecBlock().isEmpty())
			addStepExecBlock(step.getExecBlock().get(), stepNode, context, mermaidBuilder, graph);

		//Transit
		addStepTransitCall(step.getTransit(), stepNode, context, mermaidBuilder, graph);
		
		//Pop context
		if (!step.getListeners().isEmpty())
			context.getProjectedListeners().pop();
		context.getAncestorNames().pop();
		
		return stepNode;
	}
	
	//--------------------------- EXEC ---------------------------
	
	protected GraphNode parseExec(String execFunctionName, Exec exec, ParsingContext context, Optional<StringBuilder> mermaidBuilder, Map<String, GraphNode> graph) {
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
			addBatchCall(listener, execNode, new ParsingContext(), mermaidBuilder, LinkType.PROJECTED_LISTENER, graph);
		}

		if (!exec.getListeners().isEmpty())
		for (ListenerCall listener : ParsingContext.filterListeners(new Event[] { Event.EXECUTOR_STARTED, Event.EXECUTOR_FINISHED }, exec.getListeners().get()))
			addBatchCall(listener, execNode, new ParsingContext(), mermaidBuilder, LinkType.OWN_LISTENER, graph);
		
		//Push context
		if (!exec.getListeners().isEmpty())
			context.getProjectedListeners().push(exec.getListeners().get());
		context.getAncestorNames().push(execFunctionName);

		//Called function
		addCall(exec.getCalledFunctionName(), execNode, new ParsingContext(), mermaidBuilder, LinkType.NEW_CONTEXT_CALL, graph);
		
		//Pop context
		if (!exec.getListeners().isEmpty())
			context.getProjectedListeners().pop();
		context.getAncestorNames().pop();
		
		return execNode;
	}
	
	//--------------------------- TRANSIT ---------------------------
	
	protected GraphNode parseTransit(String transitFunctionName, Transit transit, ParsingContext context, Optional<StringBuilder> mermaidBuilder, Map<String, GraphNode> graph) {
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
			addBatchCall(listener, transitNode, new ParsingContext(), mermaidBuilder, LinkType.PROJECTED_LISTENER, graph);
		}

		if (!transit.getListeners().isEmpty())
		for (ListenerCall listener : ParsingContext.filterListeners(new Event[] { Event.TRANSITIONER_STARTED, Event.TRANSITIONER_FINISHED }, transit.getListeners().get()))
			addBatchCall(listener, transitNode, new ParsingContext(), mermaidBuilder, LinkType.OWN_LISTENER, graph);
		
		//Push context
		if (!transit.getListeners().isEmpty())
			context.getProjectedListeners().push(transit.getListeners().get());
		context.getAncestorNames().push(transitFunctionName);

		//Called function
		addCall(transit.getCalledFunctionName(), transitNode, new ParsingContext(), mermaidBuilder, LinkType.NEW_CONTEXT_CALL, graph);
		
		//Pop context
		if (!transit.getListeners().isEmpty())
			context.getProjectedListeners().pop();
		context.getAncestorNames().pop();
		
		return transitNode;
	}
}
