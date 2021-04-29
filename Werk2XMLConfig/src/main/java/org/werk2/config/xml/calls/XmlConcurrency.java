package org.werk2.config.xml.calls;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "concurrency")
@XmlEnum
public enum XmlConcurrency {
	NON_BLOCKING,
	BLOCKING,
	SYNCHRONIZED;

    public String value() {
        return name();
    }
    
    public static XmlConcurrency fromValue(String v) {
        return valueOf(v);
    }
}
