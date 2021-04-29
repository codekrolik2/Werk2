package org.werk2.config;

public interface StepCall extends Call {
    String getStepName();
    void setStepName(String stepName);
    boolean isFirst();
    void setFirst(boolean isFirst);
}
