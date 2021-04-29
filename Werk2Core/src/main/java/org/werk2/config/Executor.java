package org.werk2.config;

import java.util.List;

public interface Executor {
    Function getFunction();
    void setFunction(Function function);
    List<ListenerCall> getListeners();
    void setListeners(List<ListenerCall> listeners);
}
