package org.werk2.config.xml.functions;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.functions.ParameterType;

@XmlType(name = "type")
@XmlEnum
public enum TypeXml {
	//JSON-like
    LONG,
    CHARACTER,
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
    FUNCTION;

    public String value() {
        return name();
    }
    
    public static TypeXml fromValue(String v) {
        return valueOf(v);
    }
    
    public static ParameterType toParameterType(TypeXml type) {
    	switch (type) {
	    	case LONG : return ParameterType.LONG;
	    	case CHARACTER : return ParameterType.CHARACTER;
	    	case DOUBLE : return ParameterType.DOUBLE;
	    	case BOOL : return ParameterType.BOOL;
	    	case STRING : return ParameterType.STRING;
	    	case BYTES : return ParameterType.BYTES;
	    	case LIST : return ParameterType.LIST;
	    	case MAP : return ParameterType.MAP;
	    	case RUNTIME : return ParameterType.RUNTIME;
	    	case STEP : return ParameterType.STEP;
	    	//case FUNCTION
    		default : return ParameterType.FUNCTION;
    	}
    }
}
