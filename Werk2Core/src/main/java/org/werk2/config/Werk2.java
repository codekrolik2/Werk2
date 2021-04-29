package org.werk2.config;

import java.util.List;

public interface Werk2 {
    Engine getEngine();
    void setEngine(Engine engine);
	List<Flow> getFlows();
	void setFlows(List<Flow> flows);
	List<Step> getSteps();
	void setSteps(List<Step> steps);
	List<Executor> getExecutors();
	void setExecutors(List<Executor> executors);
	List<Transitioner> getTransitioners();
	void setTransitioners(List<Transitioner> transitioners);
	List<Function> getListeners();
	void setListeners(List<Function> listeners);
}
