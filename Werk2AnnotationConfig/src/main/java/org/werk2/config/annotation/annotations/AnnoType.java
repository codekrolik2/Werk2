package org.werk2.config.annotation.annotations;

import org.werk2.config.functions.ParameterType;

/**
 * Parameter type
 * @author jamirov
 *
 */
public enum AnnoType {
	//JSON-like
    LONG,
    CHARACTER,
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
    FUNCTION,
    
    //Determine parameter type automatically
    AUTO;

    public String value() {
        return name();
    }
    
    public static AnnoType fromValue(String v) {
        return valueOf(v);
    }
    
    public static ParameterType toParameterType(AnnoType type) {
    	switch (type) {
    		case LONG: return ParameterType.LONG;
    		case CHARACTER : return ParameterType.CHARACTER;
    		case DOUBLE: return ParameterType.DOUBLE;
    		case BOOL: return ParameterType.BOOL;
    		case STRING: return ParameterType.STRING;
    		case BYTES: return ParameterType.BYTES;
    		case LIST: return ParameterType.LIST;
    		case DICTIONARY: return ParameterType.DICTIONARY;
		        
    		case RUNTIME: return ParameterType.RUNTIME;
		
    		case STEP: return ParameterType.STEP;
    		case FUNCTION: return ParameterType.FUNCTION;
		        
    		//case AUTO
    		default: throw new IllegalArgumentException("No direct ParameterType match to AnnoType.AUTO.");
    	}
    }
}
