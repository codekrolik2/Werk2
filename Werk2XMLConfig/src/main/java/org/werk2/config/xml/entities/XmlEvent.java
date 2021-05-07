package org.werk2.config.xml.entities;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.entities.Event;

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
    
    public static Event toEvent(XmlEvent e) {
    	switch (e) {
			case FLOW_STARTED: return Event.FLOW_STARTED;
			case FLOW_FINISHED: return Event.FLOW_FINISHED;
			case STEP_STARTED: return Event.STEP_STARTED;
			case STEP_FINISHED: return Event.STEP_FINISHED;
			case EXECUTOR_STARTED: return Event.EXECUTOR_STARTED;
			case EXECUTOR_FINISHED: return Event.EXECUTOR_FINISHED;
			case TRANSITIONER_STARTED: return Event.TRANSITIONER_STARTED;
    		//case TRANSITIONER_FINISHED
   			default: return Event.TRANSITIONER_FINISHED;
    	}
    }
}
