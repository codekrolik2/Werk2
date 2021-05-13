package org.werk2.core.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.werk2.common.OutParam;
import org.werk2.common.Ret;
import org.werk2.common.TransitRet;
import org.werk2.common.WerkException;
import org.werk2.config.calls.Call;
import org.werk2.config.calls.InBinding;
import org.werk2.config.calls.OutBinding;
import org.werk2.config.functions.FunctionParameter;
import org.werk2.config.functions.FunctionSignature;
import org.werk2.config.functions.ParameterDirection;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RawFunctionCall {
	protected RawFunction funct;
	protected FunctionSignature sign;
	protected Call call;
	protected Map<String, InBinding> inBindings;
	protected Map<String, OutBinding> outBindings;
	
	public RawFunctionCall(RawFunction func, FunctionSignature sign, Call call) {
		this.funct = func;
		this.sign = sign;
		this.call = call;
		
		inBindings = new HashMap<>();
		for (InBinding inPrm : call.getInParameters().get())
			inBindings.put(inPrm.toInParameter(), inPrm);
		
		outBindings = new HashMap<>();
		for (OutBinding outPrm : call.getOutParameters().get())
			outBindings.put(outPrm.fromOutParameter(), outPrm);
	}

	public Ret invoke(FieldsStructure fs, String caller) throws WerkException {
		String functionName = funct.getFunction().getFunctionName();
		String physicalName = funct.getFunction().getPhysicalName().get();
		
		RawSignature rawSign = funct.getSignatures().get(sign);
		Method method = rawSign.getMethod();
		ReturnType retType = rawSign.getRetType();
		
		List<? extends FunctionParameter> funParams = sign.getParameters().get();

		//Set InParams and OutParams for the call
		Object[] params = new Object[funParams.size()];
		Map<String, OutParam<?>> outs = new HashMap<>();
		for (int i = 0; i < params.length; i++) {
			FunctionParameter funParam = funParams.get(i);
			
			if (funParam.getDirection() == ParameterDirection.IN) {
				InBinding inBind = inBindings.get(funParam.getName());
				String fieldName = inBind.fromField();
				params[i] = fs.getFieldsMap().get(fieldName);
				
				if (params[i] == null)
					throw new WerkException(
						String.format(
							"Caller [%s] doesn't have field [%s : %s {%s}] for binding [%s->%s]\nfunction name [%s]\nphys [%s(...)]",
							caller, fieldName, funParam.getType(), funParam.getRuntimeType().get(), fieldName, 
							inBind.toInParameter(), functionName, physicalName
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

		try {
			Object ret = method.invoke(null, params);
			
			for (Entry<String, OutParam<?>> e : outs.entrySet()) {
				String fromParameter = e.getKey();
				OutParam<?> outValue = e.getValue();
				
				if (outValue.get() == null) {
					//Warning: outValues must be filled. Throw?
					log.warn(
						String.format(
							"Out field: [%s] wasn't initialized by WerkFunction:\nfunction name [%s]\nphys [%s(...)]",
							fromParameter, functionName, physicalName
						)
					);
				} else {
					OutBinding out = outBindings.get(fromParameter);
					if (out != null)
						fs.getFieldsMap().put(out.toField(), outValue.get());
				}
			}
			
			Ret retVal = null;
			if (retType == ReturnType.INT) {
				if (!call.getOutStatusBinding().isEmpty())
					fs.getFieldsMap().put(call.getOutStatusBinding().get(), (Integer)ret);
				
				retVal = new RetImpl((Integer)ret);
			} else if ((retType == ReturnType.RET) || (retType == ReturnType.TRANSIT)) {
				if (!call.getOutStatusBinding().isEmpty())
					fs.getFieldsMap().put(call.getOutStatusBinding().get(), ((Ret)ret).getStatus());
				
				if (!call.getOutStatusMessageBinding().isEmpty())
					fs.getFieldsMap().put(call.getOutStatusMessageBinding().get(), ((Ret)ret).getStatusMessage());

				if (retType == ReturnType.TRANSIT)
					if (!call.getOutTransitionStatusBinding().isEmpty())
						fs.getFieldsMap().put(call.getOutTransitionStatusBinding().get(), ((TransitRet)ret).getTransitionStatus());
				
				retVal = (Ret)ret;
			}
			
			return retVal;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new WerkException(e);
		}
	}
}
