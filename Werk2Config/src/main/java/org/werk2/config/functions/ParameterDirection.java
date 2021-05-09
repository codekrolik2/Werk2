package org.werk2.config.functions;

public enum ParameterDirection {
    IN,
    OUT;

    public String value() {
        return name();
    }
    
    public static ParameterDirection fromValue(String v) {
        return valueOf(v);
    }
}
