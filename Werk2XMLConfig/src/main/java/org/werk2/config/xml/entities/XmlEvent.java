package org.werk2.config.xml.entities;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "event")
@XmlEnum
public enum XmlEvent {
    FLOW_STARTED,
    FLOW_FINISHED,
    STEP_STARTED,
    STEP_FINISHED,
    EXECUTOR_STARTED,
    EXECUTOR_FINISHED,
    TRANSITIONER_STARTED,
    TRANSITIONER_FINISHED;

    public String value() {
        return name();
    }
    
    public static XmlEvent fromValue(String v) {
        return valueOf(v);
    }
}
