package org.werk2.core.config;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.werk2.common.OutParam;
import org.werk2.common.Ret;
import org.werk2.common.TransitRet;
import org.werk2.common.WerkConfigException;
import org.werk2.config.Werk2Config;
import org.werk2.config.calls.BatchCall;
import org.werk2.config.calls.Call;
import org.werk2.config.calls.Concurrency;
import org.werk2.config.calls.InBinding;
import org.werk2.config.calls.OutBinding;
import org.werk2.config.engine.Engine;
import org.werk2.config.entities.Event;
import org.werk2.config.entities.Exec;
import org.werk2.config.entities.Flow;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.entities.Step;
import org.werk2.config.entities.Transit;
import org.werk2.config.functions.Function;
import org.werk2.config.functions.FunctionParameter;
import org.werk2.config.functions.FunctionSignature;
import org.werk2.config.functions.ParameterDirection;
import org.werk2.generics.WerkParameterizedTypeParser;

public class MaterializedConfig {
	UniqueSignaturesChecker uniqueSignaturesChecker = new UniqueSignaturesChecker();
	
	public static ReturnType getMethodReturnType(Method m, String physicalFunctionName) {
		Type returnType = m.getGenericReturnType();

		//Only status, empty message
		if (returnType.equals(int.class) || returnType.equals(Integer.class))
			return ReturnType.INT;
		if (returnType.equals(Ret.class))
			return ReturnType.RET;
		if (returnType.equals(TransitRet.class))
			return ReturnType.TRANSIT;

		Class<?>[] interfaces = null;
		if (returnType instanceof ParameterizedType) {
			Type rawType = ((ParameterizedType)returnType).getRawType();
			if (rawType instanceof Class) {
				if (rawType.equals(Ret.class))
					return ReturnType.RET;
				if (rawType.equals(TransitRet.class))
					return ReturnType.TRANSIT;
				
				interfaces = ((Class<?>)rawType).getInterfaces();
			}
		}
		
		if (returnType instanceof Class)
			interfaces = ((Class<?>)returnType).getInterfaces();

		if (interfaces != null)
		for (Class<?> intrf : interfaces) {
			if (intrf.equals(Ret.class))
				return ReturnType.RET;
			if (intrf.equals(TransitRet.class))
				return ReturnType.TRANSIT;
		}

		throw new AnnotationFormatError(
				String.format(
					"WerkFunction must return %s, %s, %s or %s. Func: [%s(...)]", 
					int.class, Integer.class, Ret.class, TransitRet.class,
					physicalFunctionName
				)
			);
	}

	//Registry
	protected Engine engine = null;
	protected Map<String, Object> functionRegistry = new HashMap<>();
	
	protected Map<String, RawFunction> rawFunctions = new HashMap<>();
	protected Map<String, Exec> execs = new HashMap<>();
	protected Map<String, Transit> transits = new HashMap<>();
	protected Map<String, Step> steps = new HashMap<>();
	protected Map<String, Flow> flows = new HashMap<>();
	
	protected void checkDuplicateFunctionName(String functionName, Object func) throws WerkConfigException {
		if (functionRegistry.containsKey(functionName))
			throw new WerkConfigException(
				String.format("Function names must be unique. Name collision: [%s].\n%s\n%s",
					functionName, func, functionRegistry.get(functionName))
			);
	}
	
	/**
	 * Fill registry: Engine and Raw functions.
	 * Validate RawFunctions.
	 * 
	 * @param configs Werk configurations
	 * @throws WerkConfigException Configuration error
	 */
	protected void fillRawFunctions(List<Werk2Config> configs) throws WerkConfigException {
		try {
			//load invocation structures. Keep track of return type (to identify functions that can be transits).
			for (Werk2Config config : configs) {
				if (!config.getEngine().isEmpty()) {
					//*) There can be only 1 Engine per process (at least for now)
					if (engine != null) {
						//TODO: MB allow duplicate declaration of the same engine? (WARN vs throw)
						throw new WerkConfigException(
								String.format("Only one engine is allowed: Engine1: [%s]; Engine2: [%s].", 
									engine, config.getEngine().get())
							);
					} else {
						engine = config.getEngine().get();
						
						if (!engine.getListeners().isEmpty())
							validateListeners(
								new Event[] {
									Event.FLOW_STARTED,
									Event.FLOW_FINISHED,
									Event.STEP_STARTED,
									Event.STEP_FINISHED,
									Event.EXECUTOR_STARTED,
									Event.EXECUTOR_FINISHED,
									Event.TRANSITIONER_STARTED,
									Event.TRANSITIONER_FINISHED
								},
								engine.getListeners().get(),
								engine);
					}
				}
				
				if (!config.getRawFunctions().isEmpty()) {
					for (Function func : config.getRawFunctions().get()) {
						if (func.getPhysicalName().isEmpty())
							throw new WerkConfigException(
								String.format("Raw function can't have empty physicalName: [%s]", 
												func)
							);
						
						//TODO: MB allow duplicate declarations of the same function? (WARN vs throw)
						String physicalName = func.getPhysicalName().get();
						
						//*) Function names are unique. Physical can't clash with logical as well.
						checkDuplicateFunctionName(func.getFunctionName(), func);
						checkDuplicateFunctionName(physicalName, func);
						
						Map<FunctionSignature, RawSignature> rawSignatures = new HashMap<>();
						for (FunctionSignature signature : func.getSignatures()) {
							//find static method of a class
							int lastInd = physicalName.lastIndexOf(".");
							String className = physicalName.substring(0, lastInd);
							String functionName = physicalName.substring(lastInd + 1);
							
							Class<?> cls = WerkParameterizedTypeParser.classForName(className);
							Class<?>[] paramTypes;
							if (signature.getParameters().isEmpty()) {
								paramTypes = new Class<?>[] {};
							} else {
								List<? extends FunctionParameter> funParams = signature.getParameters().get();
								paramTypes = new Class<?>[funParams.size()]; 
								for (int i = 0; i < paramTypes.length; i++) {
									FunctionParameter funParam = funParams.get(i);
									//In case with raw functions it can't be empty even for basic types, 
									//because it's a unique definition of method signature
									if (funParam.getRuntimeType().isEmpty())
										throw new WerkConfigException(
											String.format("Raw function parameter can't have empty runtime type: prm [%s] func [%s,]", 
												funParam.getName(), func)
										);
									
									if (funParam.getDirection() == ParameterDirection.IN)
										paramTypes[i] = WerkParameterizedTypeParser.classForName(funParam.getRuntimeType().get());
									else //if (funParams.get(i).getDirection() == ParameterDirection.OUT)
										paramTypes[i] = OutParam.class;
								}
							}

							//Get method with a provided signature
							Method method = cls.getMethod(functionName, paramTypes);
							ReturnType rt = getMethodReturnType(method, physicalName);
							
							//TODO: check that generic parameters types match, warn if they don't
							
							RawSignature rawSignature = new RawSignature(method, rt);
							rawSignatures.put(signature, rawSignature);
						}
		 				
						RawFunction rawFunction = new RawFunction(func, rawSignatures);
						rawFunctions.put(func.getFunctionName(), rawFunction);
						rawFunctions.put(func.getPhysicalName().get(), rawFunction);
	
						functionRegistry.put(func.getFunctionName(), func);
						functionRegistry.put(func.getPhysicalName().get(), func);
					}
				}
			}
		} catch(Exception e) {
			if (e instanceof WerkConfigException)
				throw (WerkConfigException)e;
			else
				throw new WerkConfigException(e);
		}
	}
	
	/**
	 * Fill registry: Execs, Transits, Steps and Flows.
	 * 
	 * @param configs Werk configurations
	 * @throws WerkConfigException Configuration error
	 */
	protected void fillLogicalFunctions(List<Werk2Config> configs) throws WerkConfigException {
		//Fill functions maps and check for name collisions
		for (Werk2Config config : configs) {
			//1. Fill registry: Transit and exec functions
			if (!config.getExecs().isEmpty()) {
				for (Exec exec : config.getExecs().get()) {
					checkDuplicateFunctionName(exec.getExecFunctionName(), exec);
					functionRegistry.put(exec.getExecFunctionName(), exec);
					execs.put(exec.getExecFunctionName(), exec);
				}
			}
			
			if (!config.getTransits().isEmpty()) {
				for (Transit transit : config.getTransits().get()) {
					checkDuplicateFunctionName(transit.getTransitFunctionName(), transit);
					functionRegistry.put(transit.getTransitFunctionName(), transit);					
					transits.put(transit.getTransitFunctionName(), transit);
				}
			}
			
			//2. Fill registry: Load Steps
			if (!config.getSteps().isEmpty()) {
				for (Step step : config.getSteps().get()) {
					if (step.getFunction().getPhysicalName().isEmpty())
						throw new WerkConfigException(
							String.format("Step is a logical function and can't have physicalName: [%s]", 
								step)
						);

					checkDuplicateFunctionName(step.getFunction().getFunctionName(), step);
					functionRegistry.put(step.getFunction().getFunctionName(), step);
					steps.put(step.getFunction().getFunctionName(), step);
				}
			}
			
			//3. Load Flows
			if (!config.getFlows().isEmpty()) {
				for (Flow flow : config.getFlows().get()) {
					if (flow.getFunction().getPhysicalName().isEmpty())
						throw new WerkConfigException(
							String.format("Flow is a logical function and can't have physicalName: [%s]", 
								flow)
						);

					checkDuplicateFunctionName(flow.getFunction().getFunctionName(), flow);
					functionRegistry.put(flow.getFunction().getFunctionName(), flow);
					flows.put(flow.getFunction().getFunctionName(), flow);
				}
			}
		}
	}
	
	protected void validateFunctionName(String functionName, Object o) throws WerkConfigException {
		if (!functionRegistry.containsKey(functionName))
			throw new WerkConfigException(
				String.format("Function doesn't exist: %s. [%s]",
				functionName, o)
			);
	}
	
	protected void validateRawTransitionerFunction(String calledFunctionName, Object o) throws WerkConfigException {
		if (!steps.containsKey(calledFunctionName) && !transits.containsKey(calledFunctionName)) {
			RawFunction rawFunction = rawFunctions.get(calledFunctionName);
			if (rawFunction == null)
				throw new WerkConfigException(
					String.format("Transitioner's underlying function should be Step, Raw or other Transitioner: %s. [%s]",
						calledFunctionName, o)
				);
			
			for (RawSignature rawSignature : rawFunction.getSignatures().values())
				if (rawSignature.retType == ReturnType.TRANSIT)
					return;

			throw new WerkConfigException(
				String.format("Transitioner's underlying raw function should return [%s]: %s. [%s]",
					TransitRet.class, calledFunctionName, o)
			);
		}
	}
	
	protected void verifyTransitionerFunction(String functionName, Object o) throws WerkConfigException {
		//Transitioner function is either a Transit
		if (transits.containsKey(functionName)) return;
		//Or a Step
		if (steps.containsKey(functionName)) return;
		//Or a transitioner RawFunciton
		validateRawTransitionerFunction(functionName, o);
	}
	
	protected void validateListeners(Event[] events, List<? extends ListenerCall> listeners, Object o) throws WerkConfigException {
		Set<Event> eventSet = new HashSet<>(Arrays.asList(events));
		for (ListenerCall listener : listeners) {
			for (Event event : listener.getEvents()) {
				if (!eventSet.contains(event))
					throw new WerkConfigException(
						String.format("Listener to Event [%s] is not allowed to be atached to Object [%s] Listener [%s]",
							event, o, listener)
					);
			}
			
			validateBatchCall(listener, o);
		}
	}

	protected void validateBatchCall(BatchCall batch, Object o) throws WerkConfigException {
		if (!batch.getCalls().isEmpty())
			for (Call call : batch.getCalls().get())
				validateCall(call, false, o);
		
		if (!batch.getBatches().isEmpty())
			for (BatchCall childBatch : batch.getBatches().get())
				validateBatchCall(childBatch, o);
	}

	protected Function getFunction(String functionName, Object o) throws WerkConfigException {
		validateFunctionName(functionName, o);

		if (rawFunctions.containsKey(functionName))
			return rawFunctions.get(functionName).getFunction();
		if (steps.containsKey(functionName))
			return steps.get(functionName).getFunction();
		if (flows.containsKey(functionName))
			return flows.get(functionName).getFunction();

		if (execs.containsKey(functionName)) {
			//Execs can refer to other Execs, Transits, Steps, Flows or RawFunctions
			return getFunction(execs.get(functionName).getCalledFunctionName(), o);
		}
		
		if (transits.containsKey(functionName)) {
			Transit transit = null;
			//Transits can refer to other Transits, Steps or transit RawFunctions
			while (transits.containsKey(functionName)) {
				transit = transits.get(functionName);
				functionName = transit.getCalledFunctionName();
			}
			
			if (steps.containsKey(functionName))
				return steps.get(functionName).getFunction();

			//At this point it can only be a RawFunciton
			validateRawTransitionerFunction(functionName, transit);
			RawFunction rawFunction = rawFunctions.get(functionName);
			return rawFunction.getFunction();
		}
		
		throw new WerkConfigException(
				String.format("Function doesn't exist: %s. [%s]",
				functionName, o)
			);
	}
	
	//*) TODO: check type safety of calls
	//TODO: check that generic parameters types match, warn if they don't
	protected void validateCall(Call call, boolean isTransit, Object o) throws WerkConfigException {
		String functionName = call.getFunctionName();
		if (isTransit) {
			//Transit call can't be non-blocking
			if (!call.getConcurrency().isEmpty() && call.getConcurrency().get() == Concurrency.NON_BLOCKING)
				throw new WerkConfigException(
					String.format("Transitioner call can't be NON_BLOCKING. [%s] [%s]",
							call, o)
				);
			
			//make sure it's a transit function that's being called
			verifyTransitionerFunction(functionName, o);
		}

		Function function = getFunction(functionName, o);
		
		//All InParameters must be bound
	    List<InBinding> inParameters;
		//OutParameters may or may not be bound
	    //TODO: check signatures: signature defined by in parameters only.
	    //e.g. f(@In int i1, @In int i2) is the same as f(@In int i1, @In int i2, @Out boolean o1) 
	    List<OutBinding> outParameters;

	    
	}
	
	protected void checkSignatures(List<Werk2Config> configs) throws WerkConfigException {
		for (Werk2Config config : configs) {
			if (!config.getFlows().isEmpty())
				for (Flow flow : config.getFlows().get())
					uniqueSignaturesChecker.checkFunctionSignatures(flow.getFunction());
			
			if (!config.getSteps().isEmpty())
				for (Step step : config.getSteps().get())
					uniqueSignaturesChecker.checkFunctionSignatures(step.getFunction());
			
			if (!config.getRawFunctions().isEmpty())
				for (Function rawFunction : config.getRawFunctions().get())
					uniqueSignaturesChecker.checkFunctionSignatures(rawFunction);
		}
	}
	
	public MaterializedConfig(List<Werk2Config> configs) throws WerkConfigException {
		//Check for duplicate signatures.
	    //Signature is defined by in parameters, out parameter don't matter.
	    //e.g. f(@In int i1, @In int i2) is the same as f(@In int i1, @In int i2, @Out boolean o1) 
		//and will be considered a collision
		checkSignatures(configs);

		//Raw functions declarations need to be matched with physical functions
		fillRawFunctions(configs);
		
		//Since Flows and Steps, as well as Execs and Transits can be used as underlying functions or listeners,
		//	we need to build the entire registry of functions before checking the referential structure and call integrity.
		//Raw functions initialized above are leaf-level and can be initialized and verified independently.
		fillLogicalFunctions(configs);
		
		//!!!!!!!!!!!!!!!!!!!!!!1
		//*) TODO: here at this point in code make sure the Call Indirection Graph is Acyclic
		//		Call indirection graph not to be confused with Flow Transition Graph, latter can be acyclic.
		//*) MB: Union-find approach? not much benefit since it's initialization-only workload
		//!!!!!!!!!!!!!!!!!!!!!!1
		
		for (Werk2Config config : configs) {
			//1. Transit and exec functions
			if (!config.getExecs().isEmpty()) {
				for (Exec exec : config.getExecs().get()) {
					//verify that CalledFunction exists
					validateFunctionName(exec.getCalledFunctionName(), exec);

					//validate Listeners calls and type safety
					if (!exec.getListeners().isEmpty())
						validateListeners(
							new Event[] { 
								Event.EXECUTOR_STARTED, 
								Event.EXECUTOR_FINISHED 
							},
							exec.getListeners().get(),
							exec);
				}
			}
			
			if (!config.getTransits().isEmpty()) {
				for (Transit transit : config.getTransits().get()) {
					//verify that CalledFunction exists
					validateFunctionName(transit.getCalledFunctionName(), transit);

					//make sure transit functions have the right return type (TransitRet)
					//i.e. either points to Step, to other Transitioner or 
					//to a RawFunction with at least one signature that returns TransitRet
					verifyTransitionerFunction(transit.getCalledFunctionName(), transit);
					
					//validate Listeners calls and type safety
					if (!transit.getListeners().isEmpty())
						validateListeners(
							new Event[] { 
								Event.TRANSITIONER_STARTED, 
								Event.TRANSITIONER_FINISHED 
							},
							transit.getListeners().get(),
							transit);
				}
			}
			
			//2. Load Steps
			if (!config.getSteps().isEmpty()) {
				for (Step step : config.getSteps().get()) {
					//steps
					//step.getFunction().getFunctionName(), step
					
					//TODO: validate Transitioner call and type safety
					validateCall(step.getTransit(), true, step);
					
					if (!step.getExecBlocks().isEmpty()) {
						//TODO: validate ExecutionBlock calls and type safety
						for (BatchCall block : step.getExecBlocks().get())
							validateBatchCall(block, step);
					}
					
					//validate Listeners calls and type safety
					if (!step.getListeners().isEmpty())
						validateListeners(
							new Event[] {
								Event.STEP_STARTED,
								Event.STEP_FINISHED,
								Event.EXECUTOR_STARTED,
								Event.EXECUTOR_FINISHED,
								Event.TRANSITIONER_STARTED,
								Event.TRANSITIONER_FINISHED
							},
							step.getListeners().get(),
							step);
				}
			}
			
			//3. Load Flows
			if (!config.getFlows().isEmpty()) {
				for (Flow flow : config.getFlows().get()) {
					//flows
					//flow.getFunction().getFunctionName(), flow
					
					//TODO: validate FirstStep call and type safety
					//TODO: validate Steps calls and type safety
					//TODO: make sure Steps calls and type safety
					//TODO: for Flow-specific StepName make sure all names are unique
					
					//validate Listeners calls and type safety
					if (!flow.getListeners().isEmpty())
						validateListeners(
							new Event[] {
								Event.FLOW_STARTED,
								Event.FLOW_FINISHED,
								Event.STEP_STARTED,
								Event.STEP_FINISHED,
								Event.EXECUTOR_STARTED,
								Event.EXECUTOR_FINISHED,
								Event.TRANSITIONER_STARTED,
								Event.TRANSITIONER_FINISHED
							},
							flow.getListeners().get(),
							flow);
				}
			}
		}
	}
}
