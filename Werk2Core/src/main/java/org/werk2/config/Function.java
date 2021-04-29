package org.werk2.config;

import java.util.List;

public interface Function {
    List<FunctionSignature> getSignatures();
    void setSignatures(List<FunctionSignature> signatures);
    String getFunctionName();
    void setFunctionName(String functionName);
}
