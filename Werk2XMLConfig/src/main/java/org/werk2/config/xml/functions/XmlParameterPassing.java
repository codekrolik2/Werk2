package org.werk2.config.xml.functions;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

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
}
