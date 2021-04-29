package org.werk2.config;

import java.util.List;

public interface Engine {
    List<Parameter> getEngineParameters();
    void setEngineParameters(List<Parameter> engineParameters);
    List<ListenerCall> getListeners();
    void setListeners(List<ListenerCall> listeners);
}
