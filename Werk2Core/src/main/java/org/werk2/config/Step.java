package org.werk2.config;

import java.util.List;

public interface Step {
    Function getFunction();
    void setFunction(Function function);
    List<Call> getExecutorBlock();
    void setExecutorBlock(List<Call> executorBlock);
    Call getTransitioner();
    void setTransitioner(Call transitioner);
    List<ListenerCall> getListeners();
    void setListeners(List<ListenerCall> listeners);
}
