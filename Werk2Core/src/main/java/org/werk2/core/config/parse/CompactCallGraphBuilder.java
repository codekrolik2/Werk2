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
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.entities.StepCall;
import org.werk2.core.config.cycles.GraphNode;

public class CompactCallGraphBuilder extends GraphBuilderBase {
	protected Map<String, GraphNodeWLst> initGraph = new HashMap<>();
	
	public CompactCallGraphBuilder(List<Werk2Config> configs) {
		super(configs, false);
		
		GraphNodeWLst engineNode = new GraphNodeWLst(ENGINE_NAME);
		addListeners(engineNode, engine.getListeners());
		initGraph.put(ENGINE_NAME, engineNode);
	}
	
	//--------------------------- CALL GRAPH BUILDERS ---------------------------
	
	/**
	 * All functions in direct call (Engine) context.
	 * 
	 * @return Call graph in Mermaid format
	 */
	/*public Map<String, GraphNode> buildEverything(Optional<StringBuilder> builder) {
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
	}*/

	/**
	 * Flows as starting points and whatever is discoverable from there.
	 * 
	 * @return Call graph in Mermaid format
	 */
	/*public Map<String, GraphNode> buildAllFlows(Optional<StringBuilder> builder) {
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
	}*/

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
					+ "classDiagram\n");
		
		ParsingContext context = new ParsingContext();
		if (!engine.getListeners().isEmpty())
			context.getProjectedListeners().push(engine.getListeners().get());
		context.getAncestorNames().push(ENGINE_NAME);
		
		Map<String, GraphNodeWLst> graph = new HashMap<>(initGraph);
		GraphNodeWLst engineNode = graph.get(ENGINE_NAME);
		
		//Add engine
		//Add link to function
		
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
	/*public Map<String, GraphNode> buildStandaloneFunction(String functionName, Optional<StringBuilder> builder) {
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
	}*/

	//------
	
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
	
	//--------------------------- GENERIC WERK FUNCTIONS ---------------------------

	protected GraphNodeWLst parseFunction(String functionName, ParsingContext context, Optional<StringBuilder> mermaidBuilder, Map<String, GraphNodeWLst> graph) {
		String fullFunctionName = context.getNamePrefix() + functionName;
		GraphNodeWLst functionNode = graph.get(fullFunctionName);
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

	protected void addCall(String functionName, GraphNodeWLst node, ParsingContext parsingContext, 
			Optional<StringBuilder> mermaidBuilder, LinkType linkType, Map<String, GraphNodeWLst> graph) {
		GraphNodeWLst callNode = parseFunction(functionName, parsingContext, mermaidBuilder, graph);
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

	protected void addCall(Call c, GraphNodeWLst node, ParsingContext parsingContext, Optional<StringBuilder> mermaidBuilder, LinkType linkType, Map<String, GraphNodeWLst> graph) {
		String functionName = c.getFunctionName();
		if (c instanceof StepCall) {
			StepCall stepCall = (StepCall)c;
			if (!stepCall.getStepAlias().isEmpty())
				functionName = stepCall.getStepAlias().get() + "/" + functionName;
		}
		
		addCall(functionName, node, parsingContext, mermaidBuilder, linkType, graph);
	}
	
	protected void parseDirectFunctionCall(String functionName, GraphNodeWLst engineNode, ParsingContext engineContext, 
			Optional<StringBuilder> mermaidBuilder, Map<String, GraphNodeWLst> graph) {
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

	//--------------------------- LISTENERS ---------------------------

	protected void addListener(GraphNodeWLst node, Call listener, Event event) {
		List<String> listeners = node.listeners.get(event);
		if (listeners == null) {
			listeners = new ArrayList<>();
			node.listeners.put(event, listeners);
		}
		
		listeners.add(listener.getFunctionName());
	}
	
	protected void addListeners(GraphNodeWLst node, BatchCall batch, List<? extends Event> eventList) {
		if (!batch.getCalls().isEmpty())
			for (Call c : batch.getCalls().get())
				for (Event event : eventList)
					addListener(node, c, event);
		
		if (!batch.getBatches().isEmpty())
			for (BatchCall b : batch.getBatches().get())
				addListeners(node, b, eventList);
	}
	
	protected void addListeners(GraphNodeWLst node, Optional<? extends List<? extends ListenerCall>> listeners) {
		if (!listeners.isEmpty()) {
			for (ListenerCall listener : listeners.get()) {
				addListeners(node, listener, listener.getEvents());
			}
		}
	}
}
