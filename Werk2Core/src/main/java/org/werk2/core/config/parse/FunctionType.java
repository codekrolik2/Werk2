package org.werk2.core.config.parse;

public enum FunctionType {
	FLOW,
	STEP,
	TRANSIT,
	EXEC,
	RAW_EXEC_FUNCTION,
	RAW_TRANSIT_FUNCTION;

    public String value() {
        return name();
    }
    
    public static FunctionType fromValue(String v) {
        return valueOf(v);
    }
}
