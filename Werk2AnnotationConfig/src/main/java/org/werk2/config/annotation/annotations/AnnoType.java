package org.werk2.config.annotation.annotations;

import org.werk2.config.functions.WerkParameterType;

/**
 * Parameter type
 * @author jamirov
 *
 */
public enum AnnoType {
	//JSON-like
    LONG,
    CHAR,
    DOUBLE,
    BOOL,
    STRING,
    BYTES,
    LIST,
    MAP,
    
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
    
    public static WerkParameterType toParameterType(AnnoType type) {
    	switch (type) {
    		case LONG: return WerkParameterType.LONG;
    		case CHAR : return WerkParameterType.CHAR;
    		case DOUBLE: return WerkParameterType.DOUBLE;
    		case BOOL: return WerkParameterType.BOOL;
    		case STRING: return WerkParameterType.STRING;
    		case BYTES: return WerkParameterType.BYTES;
    		case LIST: return WerkParameterType.LIST;
    		case MAP: return WerkParameterType.MAP;
		    
    		case RUNTIME: return WerkParameterType.RUNTIME;
		
    		case STEP: return WerkParameterType.STEP;
    		case FUNCTION: return WerkParameterType.FUNCTION;
		    
    		//case AUTO
    		default: throw new IllegalArgumentException("No direct ParameterType match to AnnoType.AUTO.");
    	}
    }
}
