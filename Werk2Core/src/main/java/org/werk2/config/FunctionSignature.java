package org.werk2.config;

import java.util.List;

public interface FunctionSignature {
    List<TypedParameter> getInParameters();
    void setInParameters(List<TypedParameter> inParameters);
    List<TypedParameter> getOutParameters();
    void setOutParameters(List<TypedParameter> outParameters);
}
