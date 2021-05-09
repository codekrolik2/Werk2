package org.werk2.config.xml.functions;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.functions.ParameterDirection;

@XmlType(name = "direction")
@XmlEnum
public enum XmlParameterDirection {
    IN,
    OUT;

    public String value() {
        return name();
    }
    
    public static XmlParameterDirection fromValue(String v) {
        return valueOf(v);
    }
    
    public static ParameterDirection toParameterDirection(XmlParameterDirection p) {
    	switch (p) {
    		case IN : return ParameterDirection.IN;
    		//case OUT
    		default : return ParameterDirection.OUT;
    	}
    }
}
