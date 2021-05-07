package org.werk2.config.xml.calls;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.calls.Concurrency;

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
    
    public static Concurrency toConcurrency(XmlConcurrency c) {
    	switch (c) {
    		case BLOCKING : return Concurrency.BLOCKING;
    		case NON_BLOCKING : return Concurrency.NON_BLOCKING;
    		//case SYNCHRONIZED
    		default : return Concurrency.SYNCHRONIZED;
    	}
    }
}
