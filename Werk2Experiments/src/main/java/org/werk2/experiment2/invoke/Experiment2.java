package org.werk2.experiment2.invoke;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.werk2.common.OutParam;
import org.werk2.common.Ret;
import org.werk2.common.TransitRet;
import org.werk2.config.annotation.scan.WerkAnnotationScanner;
import org.werk2.config.calls.Call;
import org.werk2.config.calls.Concurrency;
import org.werk2.config.calls.InBinding;
import org.werk2.config.calls.OutBinding;
import org.werk2.config.functions.Function;
import org.werk2.config.functions.FunctionParameter;
import org.werk2.config.functions.FunctionSignature;
import org.werk2.config.functions.ParameterDirection;
import org.werk2.core.config.FieldsStructure;
import org.werk2.core.config.MaterializedConfig;
import org.werk2.core.config.ReturnType;
import org.werk2.generics.WerkParameterizedTypeParser;

import lombok.NonNull;

public class Experiment2 {
	
	public void invokePhysicalFunction(@NonNull FieldsStructure fs, @NonNull Call c, 
			@NonNull Function f, @NonNull FunctionSignature s, String caller) 
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		//TODO: Exact function signature should be determined on startup for all calls
		List<? extends FunctionParameter> funParams = s.getParameters().get();

		String physicalName = f.getPhysicalName().get();
		int lastInd = physicalName.lastIndexOf(".");
		String className = physicalName.substring(0, lastInd);
		String functionName = physicalName.substring(lastInd + 1);
		
		//TODO: Methods should be discovered and cached at the same time.
		Class<?> cls = WerkParameterizedTypeParser.classForName(className);
		Class<?>[] paramTypes = new Class<?>[funParams.size()]; 
		for (int i = 0; i < paramTypes.length; i++) {
			FunctionParameter funParam = funParams.get(i);
			if (funParam.getDirection() == ParameterDirection.IN)
				paramTypes[i] = WerkParameterizedTypeParser.classForName(funParam.getRuntimeType().get());
			else //if (funParams.get(i).getDirection() == ParameterDirection.OUT) {
				paramTypes[i] = OutParam.class;
		}
		
		Method m = cls.getMethod(functionName, paramTypes);

		//TODO: Method's return type also should be precached.
		ReturnType rt = MaterializedConfig.getMethodReturnType(m, physicalName);
		
		//TODO: In and Out Parameter bindings should be put in Maps for speedy lookup.
		//TODO: Parameter Type Safety should be checked on startup
		Map<String, InBinding> inBindings = new HashMap<>();
		for (InBinding inPrm : c.getInParameters().get())
			inBindings.put(inPrm.toInParameter(), inPrm);
		
		Map<String, OutBinding> outBindings = new HashMap<>();
		for (OutBinding outPrm : c.getOutParameters().get())
			outBindings.put(outPrm.fromOutParameter(), outPrm);
		
		Object[] params = new Object[funParams.size()];
		Map<String, OutParam<?>> outs = new HashMap<>();
		for (int i = 0; i < params.length; i++) {
			FunctionParameter funParam = funParams.get(i);
			
			if (funParam.getDirection() == ParameterDirection.IN) {
				InBinding inBind = inBindings.get(funParam.getName());
				String fieldName = inBind.fromField();
				params[i] = fs.getFieldsMap().get(fieldName);
				
				if (params[i] == null)
					throw new NoSuchFieldException(
						String.format(
							"Caller [%s] doesn't have field [%s : %s {%s}] for binding [%s->%s]\nfunction name [%s]\nphys [%s(...)]",
							caller, fieldName, funParam.getType(), funParam.getRuntimeType().get(), fieldName, inBind.toInParameter(), f.getFunctionName(), physicalName
						)
					);
			} else //if (funParams.get(i).getDirection() == ParameterDirection.OUT) {
			{
				@SuppressWarnings("rawtypes")
				OutParam<?> outParam = new OutParam();
				params[i] = outParam;
				outs.put(funParam.getName(), outParam);
			}
		}
		
		Object ret = m.invoke(null, params);

		if (rt == ReturnType.INT) {
			if (!c.getOutStatusBinding().isEmpty())
				fs.getFieldsMap().put(c.getOutStatusBinding().get(), (Integer)ret);
		} else if ((rt == ReturnType.RET) || (rt == ReturnType.TRANSIT)) {
			if (!c.getOutStatusBinding().isEmpty())
				fs.getFieldsMap().put(c.getOutStatusBinding().get(), ((Ret)ret).getStatus());
			if (!c.getOutStatusMessageBinding().isEmpty())
				fs.getFieldsMap().put(c.getOutStatusMessageBinding().get(), ((Ret)ret).getStatusMessage());

			if (rt == ReturnType.TRANSIT)
				if (!c.getOutTransitionStatusBinding().isEmpty())
					fs.getFieldsMap().put(c.getOutTransitionStatusBinding().get(), ((TransitRet)ret).getTransitionStatus());
		}
		
		for (Entry<String, OutParam<?>> e : outs.entrySet()) {
			String fromParameter = e.getKey();
			OutParam<?> outValue = e.getValue();
			
			if (outValue.get() == null) {
				//TODO: outValues must be filled?? throw?
				System.out.println(
					String.format(
						"Out field: [%s] wasn't initialized by WerkFunction:\nfunction name [%s]\nphys [%s(...)]",
						fromParameter, f.getFunctionName(), physicalName
					)
				);
			} else {
				OutBinding out = outBindings.get(fromParameter);
				if (out != null)
					fs.getFieldsMap().put(out.toField(), outValue.get());
			}
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, 
		NoSuchMethodException, SecurityException, IllegalAccessException, 
		IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		String functionName = "org.werk2.experiments.funcitons.TestFunc.func1";
		
		Function f = null;
		FunctionSignature s = null;

		List<Function> functions = new WerkAnnotationScanner().loadRawFunctions();
		for (Function func : functions) {
			if (func.getFunctionName().equals(functionName)) {
				f = func;
				s = f.getSignatures().get(0);
			}
		}

		InBinding[] ins = new InBinding[] {
			new InPrm("i", "i"),
			new InPrm("c", "c"),
			new InPrm("lst1", "lst1"),
			new InPrm("mapx", "mapx")
		};
		OutBinding[] outs = new OutBinding[] {
			new OutPrm("out1", "out1")
		};
		Call c = new CallProto(functionName,
			Arrays.asList(ins),
			Arrays.asList(outs),
			"outStatus",
			"outMessage",
			"transition",
			Concurrency.SYNCHRONIZED
		);

		FieldsStructure fs = new StepProto();
		Map<String, Object> fields = fs.getFieldsMap();
		fields.put("i", 150);
		fields.put("c", 'z');

		//XXX NOT new boolean[] { ... }
		List<Boolean> lst1 = Arrays.asList(new Boolean[] { true, false, false, true } );
		fields.put("lst1", lst1);
		
		Map<String, List<Integer>> mapx = new HashMap<>();
		mapx.put("row1", Arrays.asList(new Integer[] { 11, 12, 13, 15, 14 } ));
		mapx.put("row2", Arrays.asList(new Integer[] { 21, 22, 23, 25, 24 } ));
		mapx.put("row3", Arrays.asList(new Integer[] { 31, 32, 33, 35, 34 } ));
		mapx.put("row4", Arrays.asList(new Integer[] { 41, 42, 43, 45, 44 } ));
		mapx.put("row5", Arrays.asList(new Integer[] { 51, 52, 53, 55, 54 } ));
		
		fields.put("mapx", mapx);

		Experiment2 ex2 = new Experiment2();
		
		System.out.println("Invocation starts: " + functionName + "(...)");
		System.out.println("----------------------------------");
		ex2.invokePhysicalFunction(fs, c , f, s, "Proto Experimental Step");
		System.out.println("----------------------------------");
		
		System.out.println("out1 [" + fields.get("out1") + "]");
		System.out.println("outStatus [" + fields.get("outStatus") + "]");
		System.out.println("outMessage [" + fields.get("outMessage") + "]");
		System.out.println("transition [" + fields.get("transition") + "]");
		
		//ParameterizedType t = new 
	}
}
