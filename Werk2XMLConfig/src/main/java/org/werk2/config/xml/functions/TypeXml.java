package org.werk2.config.xml.functions;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "type")
@XmlEnum
public enum TypeXml {
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
    
    public static TypeXml fromValue(String v) {
        return valueOf(v);
    }
}
