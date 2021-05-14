package org.werk2.config.xml.functions;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.functions.WerkParameterType;

@XmlType(name = "type")
@XmlEnum
public enum TypeXml {
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
    FUNCTION;

    public String value() {
        return name();
    }
    
    public static TypeXml fromValue(String v) {
        return valueOf(v);
    }
    
    public static WerkParameterType toParameterType(TypeXml type) {
    	switch (type) {
	    	case LONG : return WerkParameterType.LONG;
	    	case CHAR : return WerkParameterType.CHAR;
	    	case DOUBLE : return WerkParameterType.DOUBLE;
	    	case BOOL : return WerkParameterType.BOOL;
	    	case STRING : return WerkParameterType.STRING;
	    	case BYTES : return WerkParameterType.BYTES;
	    	case LIST : return WerkParameterType.LIST;
	    	case MAP : return WerkParameterType.MAP;
	    	case RUNTIME : return WerkParameterType.RUNTIME;
	    	case STEP : return WerkParameterType.STEP;
	    	//case FUNCTION
    		default : return WerkParameterType.FUNCTION;
    	}
    }
}
