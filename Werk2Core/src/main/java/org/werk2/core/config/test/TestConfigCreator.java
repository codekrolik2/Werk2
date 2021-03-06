package org.werk2.core.config.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.werk2.core.config.prog.ProgWerk2Config;
import org.werk2.core.config.prog.calls.ProgBatchCall;
import org.werk2.core.config.prog.calls.ProgCall;
import org.werk2.core.config.prog.engine.ProgEngine;
import org.werk2.core.config.prog.entities.ProgExec;
import org.werk2.core.config.prog.entities.ProgFlow;
import org.werk2.core.config.prog.entities.ProgListenerCall;
import org.werk2.core.config.prog.entities.ProgStep;
import org.werk2.core.config.prog.entities.ProgStepCall;
import org.werk2.core.config.prog.entities.ProgTransit;
import org.werk2.core.config.prog.functions.ProgFunction;
import org.werk2.core.config.prog.functions.ProgFunctionSignature;

public class TestConfigCreator {
	public Call call(String functionName) {
		return new ProgCall(Optional.empty(), functionName, Optional.empty(), 
				Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}

	public BatchCall batchCall(String[] functionNames) {
		Call[] calls = new Call[functionNames.length];
		for (int i = 0; i < functionNames.length; i++)
			calls[i] = call(functionNames[i]);
		
		return new ProgBatchCall(Optional.empty(), Optional.of(Arrays.asList(calls)),
				Optional.empty(), Optional.empty());
	}
	
	public ListenerCall listenerCall(String functionName, Event event, List<Function> rawFunctions) {
		Optional<List<Call>> calls = Optional.empty();
		Optional<List<BatchCall>> batches = Optional.empty();
		
		if (Math.random() > 0.5)
			calls = Optional.of(Arrays.asList(new Call[] { call(functionName) }));
		else
			batches = Optional.of(Arrays.asList(new BatchCall[] { batchCall(new String[] { functionName }) }));
		
		rawFunctions.add(function(functionName));
		
		return new ProgListenerCall(Optional.empty(), 
			calls, batches, Optional.empty(), 
			Arrays.asList(new Event[] { event }), 
			Optional.empty());
	}
	
	public Function function(String functionName) {
		FunctionSignature signature = new ProgFunctionSignature(Optional.empty(), Optional.empty());
		return new ProgFunction(Optional.empty(), functionName, Optional.of(functionName),
				Arrays.asList(new FunctionSignature[] { signature }));
	}
	
	public Exec exec(int number, String calledFunction, boolean overrideListeners, List<Function> rawFunctions) {
		List<ListenerCall> execListeners = Arrays.asList(new ListenerCall[] {
				listenerCall("ES_E" + number, Event.EXECUTOR_STARTED, rawFunctions),
				listenerCall("EF_E" + number, Event.EXECUTOR_FINISHED, rawFunctions)
			});
		
		return new ProgExec(Optional.empty(), "Executor" + number, calledFunction, 
				Optional.of(execListeners), Optional.of(overrideListeners));
	}
	
	public Transit transit(int number, String calledFunction, boolean overrideListeners, List<Function> rawFunctions) {
		List<ListenerCall> transitListeners = Arrays.asList(new ListenerCall[] {
				listenerCall("TS_T" + number, Event.TRANSITIONER_STARTED, rawFunctions),
				listenerCall("TF_T" + number, Event.TRANSITIONER_FINISHED, rawFunctions)
			});
		
		return new ProgTransit(Optional.empty(), "Transitioner" + number, calledFunction, 
				Optional.of(transitListeners), Optional.of(overrideListeners));
	}

	public Step step(int number, String[] execs, String transit, boolean overrideListeners, List<Function> rawFunctions) {
		List<ListenerCall> stepListeners = Arrays.asList(new ListenerCall[] {
				listenerCall("SS_S" + number, Event.STEP_STARTED, rawFunctions),
				listenerCall("SF_S" + number, Event.STEP_FINISHED, rawFunctions),
				listenerCall("SIS_S" + number, Event.STEP_ITER_STARTED, rawFunctions),
				listenerCall("SIF_S" + number, Event.STEP_ITER_FINISHED, rawFunctions),
				listenerCall("ES_S" + number, Event.EXECUTOR_STARTED, rawFunctions),
				listenerCall("EF_S" + number, Event.EXECUTOR_FINISHED, rawFunctions),
				listenerCall("TS_S" + number, Event.TRANSITIONER_STARTED, rawFunctions),
				listenerCall("TF_S" + number, Event.TRANSITIONER_FINISHED, rawFunctions)
			});
		
		Optional<BatchCall> execBlock = Optional.empty();
		if ((execs != null) && (execs.length > 0))
			execBlock = Optional.of(batchCall(execs));
		
		Call transitCall = call(transit);
		
		return new ProgStep(Optional.empty(), function("Step" + number),
				execBlock,
				transitCall,
				Optional.of(stepListeners), Optional.of(overrideListeners));
	}
	
	public StepCall stepCall(String functionName) {
		return stepCall(functionName, null);
	}

	public StepCall stepCall(String functionName, String stepAlias) {
		return new ProgStepCall(Optional.empty(), functionName, Optional.empty(),
						Optional.empty(), Optional.empty(), Optional.empty(), 
						Optional.empty(), Optional.empty(), 
						stepAlias == null ? Optional.empty() : Optional.of(stepAlias));
	}
	
	public Flow flow(int number, String firstStep, List<Function> rawFunctions) {
		return flow(number, firstStep, null, rawFunctions);
	}
	
	public Flow flow(int number, String firstStep, String[] steps, List<Function> rawFunctions) {
		return flow(number, firstStep, steps, false, rawFunctions);
	}
	
	public Flow flow(int number, String firstStep, String[] steps, boolean overrideListeners, List<Function> rawFunctions) {
		List<ListenerCall> flowListeners = new ArrayList<>();
		flowListeners.addAll(
			Arrays.asList(new ListenerCall[] {
			listenerCall("FS_F" + number, Event.FLOW_STARTED, rawFunctions),
			listenerCall("FF_F" + number, Event.FLOW_FINISHED, rawFunctions),
			listenerCall("SS_F" + number, Event.STEP_STARTED, rawFunctions),
			listenerCall("SF_F" + number, Event.STEP_FINISHED, rawFunctions),
			listenerCall("SIS_F" + number, Event.STEP_ITER_STARTED, rawFunctions),
			listenerCall("SIF_F" + number, Event.STEP_ITER_FINISHED, rawFunctions),
			listenerCall("ES_F" + number, Event.EXECUTOR_STARTED, rawFunctions),
			listenerCall("EF_F" + number, Event.EXECUTOR_FINISHED, rawFunctions),
			listenerCall("TS_F" + number, Event.TRANSITIONER_STARTED, rawFunctions),
			listenerCall("TF_F" + number, Event.TRANSITIONER_FINISHED, rawFunctions)
		}));

		List<StepCall> stepList = new ArrayList<>();
		if (steps != null)
		for (String step : steps)
			stepList.add(stepCall(step));
		
		return new ProgFlow(Optional.empty(),
				function("Flow" + number),
				stepCall(firstStep),
				Optional.of(stepList),
				Optional.of(flowListeners),
				Optional.of(overrideListeners));
	}
	
	public ListenerCall FF_F2_1() {
		List<Call> callList = new ArrayList<>();
		callList.add(call("ExecFunction2"));
		callList.add(call("Transitioner2"));
		callList.add(call("Step2"));
		callList.add(call("Flow3"));
		
		Optional<List<Call>> calls = Optional.of(callList);
		Optional<List<BatchCall>> batches = Optional.empty();
		
		return new ProgListenerCall(Optional.empty(),
			calls, batches, Optional.empty(),
			Arrays.asList(new Event[] { Event.FLOW_FINISHED }),
			Optional.empty());
	}

	public Engine engine(List<Function> rawFunctions) {
		List<ListenerCall> engineListeners = Arrays.asList(new ListenerCall[] {
				listenerCall("FS_E", Event.FLOW_STARTED, rawFunctions),
				listenerCall("FF_E", Event.FLOW_FINISHED, rawFunctions),
				listenerCall("SS_E", Event.STEP_STARTED, rawFunctions),
				listenerCall("SF_E", Event.STEP_FINISHED, rawFunctions),
				listenerCall("SIS_E", Event.STEP_ITER_STARTED, rawFunctions),
				listenerCall("SIF_E", Event.STEP_ITER_FINISHED, rawFunctions),
				listenerCall("ES_E", Event.EXECUTOR_STARTED, rawFunctions),
				listenerCall("EF_E", Event.EXECUTOR_FINISHED, rawFunctions),
				listenerCall("TS_E", Event.TRANSITIONER_STARTED, rawFunctions),
				listenerCall("TF_E", Event.TRANSITIONER_FINISHED, rawFunctions)
			});
			
		return new ProgEngine(Optional.empty(), Optional.empty(), Optional.of(engineListeners));
	}
	
	@SuppressWarnings("unchecked")
	public Werk2Config buildConfig() {
	    List<Function> rawExecFunctions = new ArrayList<>();
	    rawExecFunctions.addAll(Arrays.asList(new Function[] {
    		function("ExecFunction2"),
    		function("ExecFunction3"),
    		function("ExecFunction5")
	    }));
	    
	    List<Function> rawTransitFunctions = new ArrayList<>();
	    rawTransitFunctions.addAll(Arrays.asList(new Function[] {
    		function("TransitFunction1"),
    		function("TransitFunction4")
	    }));
	    
	    List<Exec> execs = Arrays.asList(new Exec[] {
    		exec(1, "Flow2", false, rawExecFunctions),
    		exec(2, "ExecFunction3", false, rawExecFunctions),
    		//exec(3, "ExecFunction5", true, rawExecFunctions)
    		exec(3, "Step4", true, rawExecFunctions)
	    });
	    
	    List<Transit> transits = Arrays.asList(new Transit[] {
    		transit(1, "Step2", false, rawExecFunctions),
    		transit(2, "Step2", false, rawExecFunctions),
    		//transit(2, "TransitFunction4", false, rawExecFunctions),
    		transit(3, "TransitFunction1", false, rawExecFunctions)
	    });
	    
	    List<Step> steps = Arrays.asList(new Step[] {
    		step(1, new String[] { "Executor1" }, "Transitioner1", false, rawExecFunctions),
    		step(2, new String[] { "Executor2" }, "Transitioner2", false, rawExecFunctions),
    		step(4, new String[] { "Executor3" }, "TransitFunction4", true, rawExecFunctions)
	    });
	    
	    Flow flow2 = flow(2, "Step2", rawExecFunctions);
	    ((List<ListenerCall>)flow2.getListeners().get()).add(FF_F2_1());
	    
	    List<Flow> flows = Arrays.asList(new Flow[] {
	    	flow(1, "Step1", new String[] { "Step4" }, rawExecFunctions ),
	    	flow2,
	    	flow(3, "Step2", new String[] { "Transitioner3", "TransitFunction4" }, rawExecFunctions )
	    });

		Engine engine = engine(rawExecFunctions);

	    //TODO: add ExtendedSteps and ExtendedFlows
	    //	Perhaps in a separate test, to keep the test matching the structure defined in docs for reference
	    return new ProgWerk2Config(
    		Optional.empty(), 
    		Optional.of(engine),
			Optional.of(flows), 
			Optional.of(steps),
			Optional.empty(), //ExtendedFlows
			Optional.empty(), //ExtendedSteps
			Optional.of(execs), Optional.of(transits),
			Optional.of(rawExecFunctions),
			Optional.of(rawTransitFunctions)
		);
	}
}
