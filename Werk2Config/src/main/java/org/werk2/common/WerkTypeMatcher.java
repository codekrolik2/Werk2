package org.werk2.common;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.werk2.config.functions.ParameterType;

public class WerkTypeMatcher {
	public static ParameterType matchType(Type type) {
	    //TODO: add support
	    //STEP,
	    //FUNCTION;

		if (type.equals(int.class) || type.equals(Integer.class) || type.equals(long.class) || type.equals(Long.class)) {
			return ParameterType.LONG;
		} else if (type.equals(char.class) || type.equals(Character.class)) {
			return ParameterType.CHARACTER;
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			return ParameterType.DOUBLE;
		} else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			return ParameterType.BOOL;
		} else if (type.equals(String.class)) {
			return ParameterType.STRING;
		} else if (type.equals(byte[].class)) {
			return ParameterType.BYTES;
		} else {
			if (type.equals(List.class))
				return ParameterType.LIST;
			if (type.equals(Map.class))
				return ParameterType.MAP;

			Class<?>[] interfaces = null;
			if (type instanceof ParameterizedType) {
				Type rawType = ((ParameterizedType)type).getRawType();
				if (rawType instanceof Class) {
					if (rawType.equals(List.class))
						return ParameterType.LIST;
					if (rawType.equals(Map.class))
						return ParameterType.MAP;
					
					interfaces = ((Class<?>)rawType).getInterfaces();
				}
			}
			if (type instanceof Class) {
				interfaces = ((Class<?>)type).getInterfaces();
			}
			if (interfaces != null)
			for (Class<?> intrf : interfaces) {
				if (intrf.equals(List.class))
					return ParameterType.LIST;
				if (intrf.equals(Map.class))
					return ParameterType.MAP;
			}
		}

		return ParameterType.RUNTIME;
	}
}
