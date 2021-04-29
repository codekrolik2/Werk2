package org.werk2.config;

public interface TypedParameter extends Parameter {
    ConfigType getType();
    void setType(ConfigType type);
}
