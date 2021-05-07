package org.werk2.config.functions;

/**
 * Parameter passing mechanism
 * @author jamirov
 *
 */
public enum ParameterPassing {
    BY_REF,
    BY_VAL;

    public String value() {
        return name();
    }
    
    public static ParameterPassing fromValue(String v) {
        return valueOf(v);
    }
}
