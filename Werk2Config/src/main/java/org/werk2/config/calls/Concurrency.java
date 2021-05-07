package org.werk2.config.calls;

/**
 * Concurrency level of a call
 * @author jamirov
 */
public enum Concurrency {
	NON_BLOCKING,
	BLOCKING,
	SYNCHRONIZED;

    public String value() {
        return name();
    }
    
    public static Concurrency fromValue(String v) {
        return valueOf(v);
    }
}
