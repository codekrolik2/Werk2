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
import org.werk2.core.config.prog.calls.ProgBatchCall;
import org.werk2.core.config.prog.calls.ProgCall;
import org.werk2.core.config.prog.entities.ProgExec;
import org.werk2.core.config.prog.entities.ProgFlow;
import org.werk2.core.config.prog.entities.ProgStep;
import org.werk2.core.config.prog.entities.ProgStepCall;
import org.werk2.core.config.prog.entities.ProgTransit;
import org.werk2.core.config.prog.functions.ProgFunction;
import org.werk2.core.config.prog.functions.ProgFunctionSignature;

public class GraphBuilderBase {
	public static final String ENGINE_NAME = "Engine";
	public static final String ANON_PREFIX = "?";
	public static final String EXIT_TRANSIT_FUNCTION_NAME = ANON_PREFIX + "exit";
	
	protected int anonFlowCtr = 0;
	protected int anonStepCtr = 0;
	protected int anonExecCtr = 0;
	protected int anonTransitCtr = 0;
	
	protected Engine engine = null;
	protected Map<String, Flow> flows = new HashMap<>();
	protected Map<String, Step> steps = new HashMap<>();
	protected Map<String, Transit> transits = new HashMap<>();
	protected Map<String, Exec> execs = new HashMap<>();
	protected Map<String, Function> rawExecFunctions = new HashMap<>();
	protected Map<String, Function> rawTransitFunctions = new HashMap<>();

	protected Map<String, ExtendedFlow> extendedFlows = new HashMap<>();
	protected Map<String, ExtendedStep> extendedSteps = new HashMap<>();

	protected List<Werk2Config> configs;
	
	public GraphBuilderBase(List<Werk2Config> configs, boolean denormalizeExtendedFlowsAndSteps) {
		this.configs = configs;
		fillEntityCollections(configs, denormalizeExtendedFlowsAndSteps);
	}
	
	protected FunctionType getType(String functionName) {
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

	protected void fillEntityCollections(List<Werk2Config> configs, boolean denormalize) {
		FunctionSignature signature = new ProgFunctionSignature(Optional.empty(), Optional.empty());
		Function exitRawTransitFunction = new ProgFunction(Optional.empty(), EXIT_TRANSIT_FUNCTION_NAME, Optional.empty(),
				Arrays.asList(new FunctionSignature[] { signature }));
		rawTransitFunctions.put(EXIT_TRANSIT_FUNCTION_NAME, exitRawTransitFunction);

		for (Werk2Config config : configs) {
			if (!config.getEngine().isEmpty())
			if (engine == null) {
				engine = config.getEngine().get();
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
		if (denormalize) {
			for (ExtendedFlow eFlow : extendedFlows.values())
				denormalizeFlow(eFlow, flows, extendedFlows);
			
			for (ExtendedStep eStep : extendedSteps.values())
				denormalizeStep(eStep, steps, extendedSteps);
		}
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

	//--------------------------- ANONYMOUS WERK FUNCTIONS ---------------------------
	
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
}
