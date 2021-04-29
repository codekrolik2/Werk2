package org.werk2.config;

import java.util.List;

public interface Call {
    List<InBinding> getInParameters();
    void setInParameters(List<InBinding> inParameters);
    List<OutBinding> getOutParameters();
    void setOutParameters(List<OutBinding> outParameters);
    StatusBinding getStatus();
    void setStatus(StatusBinding status);
    StatusBinding getTransitionStatus();
    void setTransitionStatus(StatusBinding transitionStatus);
    String getFunctionName();
    void setFunctionName(String functionName);
}
