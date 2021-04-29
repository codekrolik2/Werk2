package org.werk2.config;

public enum Type {
    LONG,
    DOUBLE,
    BOOL,
    STRING,
    LIST,
    DICTIONARY,
    STEP_NAME,
    RUNTIME;

    public String value() {
        return name();
    }

    public static Type fromValue(String v) {
        return valueOf(v);
    }
}
