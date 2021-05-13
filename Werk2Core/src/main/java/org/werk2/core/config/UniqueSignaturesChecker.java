package org.werk2.core.config;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.werk2.common.WerkConfigException;
import org.werk2.common.WerkTypeMatcher;
import org.werk2.config.functions.Function;
import org.werk2.config.functions.FunctionParameter;
import org.werk2.config.functions.FunctionSignature;
import org.werk2.config.functions.ParameterDirection;
import org.werk2.config.functions.ParameterType;
import org.werk2.generics.WerkParameterizedTypeParser;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class UniqueSignaturesChecker {
	@RequiredArgsConstructor
	class ComparableSignature {
		@NonNull FunctionSignature functionSignature;
		Map<String, FunctionParameter> inParameters = new HashMap<>();
	}

	WerkParameterizedTypeParser werkParameterizedTypeParser = new WerkParameterizedTypeParser();

	protected boolean areSignaturesEqual(ComparableSignature sign1, ComparableSignature sign2, 
			Function function) throws WerkConfigException {
		if (sign1.inParameters.size() != sign2.inParameters.size())
			return false;
		for (Map.Entry<String, FunctionParameter> ent : sign1.inParameters.entrySet()) {
			String prmName = ent.getKey();
			FunctionParameter prm1 = ent.getValue();
			FunctionParameter prm2 = sign2.inParameters.get(prmName);
			
			if (prm2 == null)
				return false;
			if (prm1.getType() == ParameterType.RUNTIME || prm2.getType() == ParameterType.RUNTIME) {
				if (prm1.getType() == ParameterType.RUNTIME || prm2.getType() == ParameterType.RUNTIME) {
					//Make sure RuntimeTypes are set for RUNTIME parameters
					if (prm2.getRuntimeType().isEmpty()) {
						throw new WerkConfigException(
							String.format("RUNTIME parameter [%s] can't have empty RuntimeType. function: [%s]", 
							prm2.getName(), function)
						);
					}
					if (prm1.getRuntimeType().isEmpty()) {
						throw new WerkConfigException(
							String.format("RUNTIME parameter [%s] can't have empty RuntimeType. function: [%s]", 
							prm1.getName(), function)
						);
					}
					
					//Extract RuntimeTypes
					Type runtimeType2;
					try {
						runtimeType2 = werkParameterizedTypeParser.parse(prm2.getRuntimeType().get());
					} catch (ClassNotFoundException e) {
						throw new WerkConfigException(
							String.format("Class [%s] not found for RUNTIME parameter [%s]. function: [%s]", 
								prm2.getRuntimeType().get(), prm2.getName(), function)
						);
					}
					Type runtimeType1;
					try {
						runtimeType1 = werkParameterizedTypeParser.parse(prm1.getRuntimeType().get());
					} catch (ClassNotFoundException e) {
						throw new WerkConfigException(
							String.format("Class [%s] not found for RUNTIME parameter [%s]. function: [%s]", 
								prm1.getRuntimeType().get(), prm1.getName(), function)
						);
					}
					
					//First try to match as Werk ParameterTypes
					ParameterType prm1Type = WerkTypeMatcher.matchType(runtimeType1);
					ParameterType prm2Type = WerkTypeMatcher.matchType(runtimeType2);
					if (prm1Type != prm2Type)
						return false;
					
					//Try to match as RawTypes
					Type raw1 = runtimeType1 instanceof ParameterizedType ? 
							((ParameterizedType)runtimeType1).getRawType() : runtimeType1;
					Type raw2 = runtimeType2 instanceof ParameterizedType ? 
							((ParameterizedType)runtimeType2).getRawType() : runtimeType2;
					if (!raw1.equals(raw2))
						return false;
				} else {
					//If one of the parameters is RUNTIME, we compare its runtimeType to Werk ParameterType
					//prm1 to be Runtime
					if (prm2.getType() == ParameterType.RUNTIME) {
						FunctionParameter swp = prm1;
						prm1 = prm2;
						prm2 = swp;
					}
					
					if (prm1.getRuntimeType().isEmpty()) {
						throw new WerkConfigException(
							String.format("RUNTIME parameter [%s] can't have empty RuntimeType. function: [%s]", 
							prm1.getName(), function)
						);
					}
					
					Type runtimeType1;
					try {
						runtimeType1 = werkParameterizedTypeParser.parse(prm1.getRuntimeType().get());
					} catch (ClassNotFoundException e) {
						throw new WerkConfigException(
							String.format("Class [%s] not found for RUNTIME parameter [%s]. function: [%s]", 
								prm1.getRuntimeType().get(), prm1.getName(), function)
						);
					}
					
					ParameterType prm1Type = WerkTypeMatcher.matchType(runtimeType1);
					if (prm1Type != prm2.getType())
						return false;
				}
			} else if (prm1.getType() != prm2.getType())
				return false;
		}
		return true;
	}
	
	public void checkFunctionSignatures(Function function) throws WerkConfigException {
		if (function.getSignatures().isEmpty())
			return;

		List<ComparableSignature> comparisonList = new ArrayList<>(); 
		
		List<? extends FunctionSignature> signatures = function.getSignatures();
		for (FunctionSignature signature : signatures) {
			ComparableSignature newSign = new ComparableSignature(signature);
			if (!signature.getParameters().isEmpty()) {
				for (FunctionParameter parameter : signature.getParameters().get()) {
					if (parameter.getDirection() == ParameterDirection.IN) 
						newSign.inParameters.put(parameter.getName(), parameter);
				}
			}
			
			for (ComparableSignature sign : comparisonList) {
				if (areSignaturesEqual(sign, newSign, function)) {
					throw new WerkConfigException(
						String.format(
							"A function [%s] has equal signatures: \nSign1 [%s] \nSign2 [%s]",
							function.getFunctionName(), sign.functionSignature, signature
						)
					);
				}
			}
		}
	}
	
	/*public void test(List<Integer> l) {
		System.out.println("List");
	}
	
	public void test(ArrayList<Integer> l) {
		System.out.println("ArrayList");
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		UniqueSignaturesChecker c = new UniqueSignaturesChecker();
		
		List<Integer> l = new ArrayList<>();
		ArrayList<Integer> al = new ArrayList<>();
		c.test(l);
		c.test(al);
		
		c.test((ArrayList<Integer>)l);
		c.test((List<Integer>)al);
		
		c.test((ArrayList)l);
		c.test((List)al);

		List rl = new ArrayList<>();
		ArrayList ral = new ArrayList<>();
		
		c.test(rl);
		c.test(ral);

		c.test((ArrayList<Integer>)rl);
		c.test((List<Integer>)ral);
		
		c.test((ArrayList)rl);
		c.test((List)ral);
		
		System.out.println(Class.forName("java.util.ArrayList").toString());

		//Class not found
		System.out.println(Class.forName("java.util.ArrayList<Integer>").toString());
	}*/
}
