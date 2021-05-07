package org.werk2.config.functions;

/**
 * Parameter type
 * @author jamirov
 *
 */
public enum ParameterType {
	//JSON-like
    LONG,
    DOUBLE,
    BOOL,
    STRING,
    BYTES,
    LIST,
    DICTIONARY,
    
    //Runtime parameters - any type allowed in underlying language    
    RUNTIME,

    //Special type to allow Transition validation, only step function names should be accepted
    STEP,
    //Run child flows - all logical and physical function names are accepted
    FUNCTION;

    public String value() {
        return name();
    }
    
    public static ParameterType fromValue(String v) {
        return valueOf(v);
    }
}
