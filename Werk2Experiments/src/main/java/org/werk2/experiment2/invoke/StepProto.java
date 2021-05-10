package org.werk2.experiment2.invoke;

import java.util.HashMap;
import java.util.Map;

public class StepProto implements FieldsStructure {
	protected Map<String, Object> fields = new HashMap<>();

	@Override
	public Map<String, Object> getFieldsMap() {
		return fields;
	}
}
