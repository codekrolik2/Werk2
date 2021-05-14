package org.werk2.core.config;

import java.util.Map;

import org.javatuples.Pair;
import org.werk2.config.functions.WerkParameterType;

public interface FieldsStructure {
	//<Name, Pair<Type, RuntimeType>>
	Map<String, Pair<WerkParameterType, String>> getFieldsTypesMap();
	Map<String, Object> getFieldsMap();
}
