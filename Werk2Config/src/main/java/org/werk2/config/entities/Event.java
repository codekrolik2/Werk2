package org.werk2.config.entities;

/**
 * Flow lifecycle events that listeners can be bound to
 * @author jamirov
 *
 */
public enum Event {
    FLOW_STARTED,
    FLOW_FINISHED,
    STEP_STARTED,
    STEP_FINISHED,
    STEP_ITER_STARTED,
    STEP_ITER_FINISHED,
    EXECUTOR_STARTED,
    EXECUTOR_FINISHED,
    TRANSITIONER_STARTED,
    TRANSITIONER_FINISHED;

    public String value() {
        return name();
    }
    
    public static Event fromValue(String v) {
        return valueOf(v);
    }
}
