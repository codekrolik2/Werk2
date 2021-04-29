package org.werk2.config;

import java.util.List;

public interface Flow {
    String getFlowName();
    void setFlowName(String flowName);
    Function getFunction();
    void setFunction(Function function);
    List<StepCall> getSteps();
    void setSteps(List<StepCall> steps);
    List<ListenerCall> getListeners();
    void setListeners(List<ListenerCall> listeners);
}
