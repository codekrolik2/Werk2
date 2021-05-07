package org.werk2.config.xml.functions;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.functions.ParameterPassing;

@XmlType(name = "pass")
@XmlEnum
public enum XmlParameterPassing {
    BY_REF,
    BY_VAL;

    public String value() {
        return name();
    }
    
    public static XmlParameterPassing fromValue(String v) {
        return valueOf(v);
    }
    
    public static ParameterPassing toParameterPassing(XmlParameterPassing p) {
    	switch (p) {
    		case BY_REF: return ParameterPassing.BY_REF;
    		//case BY_VAL
    		default : return ParameterPassing.BY_VAL;
    	}
    }
}
