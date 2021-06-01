package org.werk2.core.config.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.Werk2Config;
import org.werk2.config.calls.BatchCall;
import org.werk2.config.calls.Call;
import org.werk2.config.calls.Concurrency;
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
import org.werk2.config.functions.FunctionSignature;
import org.werk2.core.config.cycles.GraphNode;
import org.werk2.core.config.prog.calls.ProgBatchCall;
import org.werk2.core.config.prog.calls.ProgCall;
import org.werk2.core.config.prog.entities.ProgExec;
import org.werk2.core.config.prog.entities.ProgFlow;
import org.werk2.core.config.prog.entities.ProgStep;
import org.werk2.core.config.prog.entities.ProgStepCall;
import org.werk2.core.config.prog.entities.ProgTransit;
import org.werk2.core.config.prog.functions.ProgFunction;
import org.werk2.core.config.prog.functions.ProgFunctionSignature;

/**
 * Builds full call graph from configuration(s)
 * @author jamirov
 *
 */
public class MermaidCallGraphBuilder {
	protected static String ENGINE_NAME = "Engine";
	protected static String ANON_PREFIX = "?";
	protected static String EXIT_TRANSIT_FUNCTION_NAME = ANON_PREFIX + "exit";
	
	protected Engine engine = null;
	protected Map<String, Flow> flows = new HashMap<>();
	protected Map<String, Step> steps = new HashMap<>();
	protected Map<String, Transit> transits = new HashMap<>();
	protected Map<String, Exec> execs = new HashMap<>();
	protected Map<String, Function> rawExecFunctions = new HashMap<>();
	protected Map<String, Function> rawTransitFunctions = new HashMap<>();

	protected Map<String, GraphNode> initGraph = new HashMap<>();

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
		FunctionSignature signature = new ProgFunctionSignature(Optional.empty(), Optional.empty());
		Function exitRawTransitFunction = new ProgFunction(Optional.empty(), EXIT_TRANSIT_FUNCTION_NAME, Optional.empty(),
				Arrays.asList(new FunctionSignature[] { signature }));
		rawTransitFunctions.put(EXIT_TRANSIT_FUNCTION_NAME, exitRawTransitFunction);

		Map<String, ExtendedFlow> extendedFlows = new HashMap<>();
		Map<String, ExtendedStep> extendedSteps = new HashMap<>();

		for (Werk2Config config : configs) {
			if (!config.getEngine().isEmpty())
			if (engine == null) {
				engine = config.getEngine().get();
				
				GraphNode engineNode = new GraphNode(ENGINE_NAME);
				initGraph.put(ENGINE_NAME, engineNode);
			}
			
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

			if (!config.getRawExecFunctions().isEmpty())
			for (Function rawExecFunction : config.getRawExecFunctions().get())
				rawExecFunctions.put(rawExecFunction.getFunctionName(), rawExecFunction);

			if (!config.getRawTransitFunctions().isEmpty())
			for (Function rawTransitFunction : config.getRawTransitFunctions().get())
				rawTransitFunctions.put(rawTransitFunction.getFunctionName(), rawTransitFunction);

			if (!config.getExtendedFlows().isEmpty())
			for (ExtendedFlow eFlow : config.getExtendedFlows().get())
				extendedFlows.put(eFlow.getNewFunctionName(), eFlow);

			if (!config.getExtendedSteps().isEmpty())
			for (ExtendedStep eStep : config.getExtendedSteps().get())
				extendedSteps.put(eStep.getNewFunctionName(), eStep);
		}
		
		//Denormalize ExtendedSteps and ExtendedFlows
		for (ExtendedFlow eFlow : extendedFlows.values())
			denormalizeFlow(eFlow, flows, extendedFlows);
		
		for (ExtendedStep eStep : extendedSteps.values())
			denormalizeStep(eStep, steps, extendedSteps);		
	}
	
	protected Flow denormalizeFlow(ExtendedFlow eFlow, Map<String, Flow> flows,
			Map<String, ExtendedFlow> extendedFlows) {
		String parentName = eFlow.getSuperFlowFunctionName();
		Flow parent = flows.get(parentName);
		if (parent == null)
			parent = denormalizeFlow(extendedFlows.get(parentName), flows, extendedFlows);
		
		Flow flow = mergeFlows(eFlow, parent);
		flows.put(eFlow.getNewFunctionName(), flow);
		return flow;
	}

	//denormalize ExtendedFlow
	protected Flow mergeFlows(ExtendedFlow eFlow, Flow parent) {
		Optional<? extends Doc> doc = eFlow.getDoc().isEmpty() ? parent.getDoc() : eFlow.getDoc();
		
		Function function;
		{
			Optional<? extends Doc> functionDoc = eFlow.getFunctionDoc();
					
			String functionName = eFlow.getNewFunctionName();
			Optional<String> physicalName = Optional.empty();

			List<FunctionSignature> signatures = new ArrayList<>();
			if (eFlow.getDropOldSignatures().isEmpty() || !eFlow.getDropOldSignatures().get())
				signatures.addAll(parent.getFunction().getSignatures());
			if (!eFlow.getAddSignatures().isEmpty())
				signatures.addAll(eFlow.getAddSignatures().get());
			
			function = new ProgFunction(functionDoc, functionName, physicalName, signatures);
		}
		
		StepCall firstStep = eFlow.getNewFirstStep().isEmpty() ? parent.getFirstStep() : eFlow.getNewFirstStep().get();

		List<ListenerCall> listenerCalls = new ArrayList<>();
		if (eFlow.getDropOldListeners().isEmpty() || !eFlow.getDropOldListeners().get()) {
			if (!parent.getListeners().isEmpty())
				listenerCalls.addAll(parent.getListeners().get());
		}
		if (!eFlow.getAddListeners().isEmpty())
			listenerCalls.addAll(eFlow.getAddListeners().get());
		Optional<? extends List<? extends ListenerCall>> listeners = Optional.of(listenerCalls);

		List<StepCall> stepCalls = new ArrayList<>();
		if (eFlow.getDropOldSteps().isEmpty() || !eFlow.getDropOldSteps().get()) {
			if (!parent.getSteps().isEmpty())
				stepCalls.addAll(parent.getSteps().get());
		}
		if (!eFlow.getAddSteps().isEmpty())
			stepCalls.addAll(eFlow.getAddSteps().get());
		Optional<? extends List<? extends StepCall>> steps = Optional.of(stepCalls);
		
		Optional<Boolean> overrideListeners = eFlow.getNewOverrideListeners().isEmpty() ? 
				parent.getOverrideListeners() : eFlow.getNewOverrideListeners();

		Flow denormalizedFlow = new ProgFlow(doc, function, firstStep, steps, listeners, overrideListeners);
		return denormalizedFlow;
	}

	protected Step denormalizeStep(ExtendedStep eStep, Map<String, Step> steps,
			Map<String, ExtendedStep> extendedSteps) {
		String parentName = eStep.getSuperStepFunctionName();
		Step parent = steps.get(parentName);
		if (parent == null)
			parent = denormalizeStep(extendedSteps.get(parentName), steps, extendedSteps);
		
		Step step = mergeSteps(eStep, parent);
		steps.put(eStep.getNewFunctionName(), step);
		return step;
	}

	//denormalize ExtendedStep
	protected Step mergeSteps(ExtendedStep eStep, Step parent) {
		Optional<? extends Doc> doc = eStep.getDoc().isEmpty() ? parent.getDoc() : eStep.getDoc();
		
		Function function;
		{
			Optional<? extends Doc> functionDoc = eStep.getFunctionDoc();
			String functionName = eStep.getNewFunctionName();
			Optional<String> physicalName = Optional.empty();

			List<FunctionSignature> signatures = new ArrayList<>();
			if (eStep.getDropOldSignatures().isEmpty() || !eStep.getDropOldSignatures().get())
				signatures.addAll(parent.getFunction().getSignatures());
			if (!eStep.getAddSignatures().isEmpty())
				signatures.addAll(eStep.getAddSignatures().get());
			
			function = new ProgFunction(functionDoc, functionName, physicalName, signatures);
		}
		
		Optional<BatchCall> execBlock;
		if (eStep.getDropOldExecBlock().isEmpty() || !eStep.getDropOldExecBlock().get()) {
			if (eStep.getAddExecBlock().isEmpty()) {
				execBlock = parent.getExecBlock();
			} else if (!parent.getExecBlock().isEmpty()) {
				Optional<? extends Doc> execBlockDoc = eStep.getAddExecBlock().get().getDoc();
				Optional<? extends List<? extends Call>> calls = Optional.empty();
				
				List<BatchCall> newBatchCalls = new ArrayList<>();
				newBatchCalls.add(parent.getExecBlock().get());
				newBatchCalls.add(eStep.getAddExecBlock().get());
				Optional<? extends List<? extends BatchCall>> batches = Optional.of(newBatchCalls);
				Optional<Concurrency> concurrency = Optional.of(Concurrency.SYNCHRONIZED);
				
				BatchCall newExecBlock = new ProgBatchCall(execBlockDoc, calls, batches, concurrency);
				execBlock = Optional.of(newExecBlock);
			} else
				execBlock = eStep.getAddExecBlock();
		} else
			execBlock = eStep.getAddExecBlock();

		Call transit = eStep.getNewTransit().isEmpty() ? parent.getTransit() : eStep.getNewTransit().get();
		
		List<ListenerCall> listenerCalls = new ArrayList<>();
		if (eStep.getDropOldListeners().isEmpty() || !eStep.getDropOldListeners().get()) {
			if (!parent.getListeners().isEmpty())
				listenerCalls.addAll(parent.getListeners().get());
		}
		if (!eStep.getAddListeners().isEmpty())
			listenerCalls.addAll(eStep.getAddListeners().get());
		Optional<? extends List<? extends ListenerCall>> listeners = Optional.of(listenerCalls);
		
		Optional<Boolean> overrideListeners = eStep.getNewOverrideListeners().isEmpty() ? 
				parent.getOverrideListeners() : eStep.getNewOverrideListeners();

		Step step = new ProgStep(doc, function, execBlock, transit, listeners, overrideListeners);
		return step;
	}

	protected void appendLinkType(StringBuilder builder, LinkType type) {
		builder.append( String.format("%s %s %s_\n", type.legend(), type.value(), type.legend()) );
	}
	
	//--------------------------- CALL GRAPH BUILDERS ---------------------------

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

	//--------------------------- ??? ---------------------------

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
		} else if (rawExecFunctions.containsKey(functionName)) {
			functionType = FunctionType.RAW_EXEC_FUNCTION;
		} else if (rawTransitFunctions.containsKey(functionName)) {
			functionType = FunctionType.RAW_TRANSIT_FUNCTION;
		} else
			throw new RuntimeException(
				String.format("Function name not found [%s]", functionName)
			);

		return functionType;
	}
	
	public void parseDirectFunctionCall(String functionName, GraphNode engineNode, ParsingContext engineContext, 
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
			default : {
				//case RAW_TRANSIT_FUNCTION
				Call transitCall = anonTransit(functionName);
				Call stepCall = anonStepForTransitFunction(transitCall.getFunctionName());
				Call flowCall = anonFlow(stepCall.getFunctionName());
				addCall(flowCall, engineNode, engineContext, mermaidBuilder, LinkType.PROJECTED_CALL, graph);
				break;
			}
		}
	}

	//--------------------------- ANONYMOUS FUNCTIONS ---------------------------
	
	protected Call anonFlow(String functionName) {
		anonFlowCtr++;
		String anonFlowName = ANON_PREFIX + "flow_" + anonFlowCtr;
		
		FunctionSignature signature = new ProgFunctionSignature(Optional.empty(), Optional.empty());
		Function anonFlowFunction = new ProgFunction(Optional.empty(), anonFlowName, Optional.empty(),
				Arrays.asList(new FunctionSignature[] { signature }));

		StepCall firstStepCall = new ProgStepCall(Optional.empty(), functionName, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
		Flow anonFlow = new ProgFlow(Optional.empty(), anonFlowFunction, firstStepCall, Optional.empty(), Optional.empty(), Optional.empty());

		flows.put(anonFlowName, anonFlow);
		
		return new ProgCall(Optional.empty(), anonFlowName, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}
	
	protected Call anonStepForExecFunction(String execFunctionName) {
		anonStepCtr++;
		String anonStepName = ANON_PREFIX + "step_" + anonStepCtr;
		FunctionSignature signature = new ProgFunctionSignature(Optional.empty(), Optional.empty());
		Function anonStepFunction = new ProgFunction(Optional.empty(), anonStepName, Optional.empty(),
				Arrays.asList(new FunctionSignature[] { signature }));
		
		Call execFunctionCall = new ProgCall(Optional.empty(), execFunctionName, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
		BatchCall batch = new ProgBatchCall(Optional.empty(), 
				Optional.of(Arrays.asList(new Call[] { execFunctionCall })), Optional.empty(), Optional.empty());
		
		Step anonStep = new ProgStep(Optional.empty(), 
				anonStepFunction, 
				Optional.of(batch),
				new ProgCall(Optional.empty(), EXIT_TRANSIT_FUNCTION_NAME, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()),
				Optional.empty(), Optional.empty());
		
		steps.put(anonStepName, anonStep);
		
		return new ProgCall(Optional.empty(), anonStepName, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}
	
	protected Call anonStepForTransitFunction(String transitFunctionName) {
		anonStepCtr++;
		String anonStepName = ANON_PREFIX + "step_" + anonStepCtr;
		FunctionSignature signature = new ProgFunctionSignature(Optional.empty(), Optional.empty());
		Function anonStepFunction = new ProgFunction(Optional.empty(), anonStepName, Optional.empty(),
				Arrays.asList(new FunctionSignature[] { signature }));
		
		Step anonStep = new ProgStep(Optional.empty(),
				anonStepFunction,
				Optional.empty(),
				new ProgCall(Optional.empty(), transitFunctionName, Optional.empty(), Optional.empty(),
						Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()),
				Optional.empty(), Optional.empty());
		
		steps.put(anonStepName, anonStep);
		
		return new ProgCall(Optional.empty(), anonStepName, Optional.empty(), Optional.empty(), Optional.empty(),
				Optional.empty(), Optional.empty(), Optional.empty());
	}
	
	protected Call anonTransit(String functionName) {
		anonTransitCtr++;
		String anonTransitName = ANON_PREFIX + "transit_" + anonTransitCtr;
		Transit anonTransit = new ProgTransit(Optional.empty(), anonTransitName, functionName, Optional.empty(), Optional.empty());
		transits.put(anonTransitName, anonTransit);
		
		return new ProgCall(Optional.empty(), anonTransitName, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}
	
	protected Call anonExec(String functionName) {
		anonExecCtr++;
		String anonExecName = ANON_PREFIX + "exec_" + anonExecCtr;
		Exec anonExec = new ProgExec(Optional.empty(), anonExecName, functionName, Optional.empty(), Optional.empty());
		execs.put(anonExecName, anonExec);
		
		return new ProgCall(Optional.empty(), anonExecName, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
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
