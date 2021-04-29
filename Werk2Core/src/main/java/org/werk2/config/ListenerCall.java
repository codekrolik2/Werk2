package org.werk2.config;

public interface ListenerCall extends Call {
    ConfigEvent getEvent();
    void setEvent(ConfigEvent event);
}
