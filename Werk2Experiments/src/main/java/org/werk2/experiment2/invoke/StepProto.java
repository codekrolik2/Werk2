package org.werk2.experiment2.invoke;

import java.util.HashMap;
import java.util.Map;

import org.javatuples.Pair;
import org.werk2.config.functions.ParameterType;
import org.werk2.core.config.FieldsStructure;

public class StepProto implements FieldsStructure {
	protected Map<String, Object> fields = new HashMap<>();

	@Override
	public Map<String, Object> getFieldsMap() {
		return fields;
	}

	@Override
	public Map<String, Pair<ParameterType, String>> getFieldsTypesMap() {
		// TODO Auto-generated method stub
		return null;
	}
}
