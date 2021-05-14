package org.werk2.config.xml.entities;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.entities.Flow;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.entities.StepCall;
import org.werk2.config.functions.Function;
import org.werk2.config.xml.XmlDocumented;
import org.werk2.config.xml.functions.XmlFunction;

@XmlType(name = "flow")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFlow extends XmlDocumented implements Flow {
	@XmlElement(required = true)
    public XmlFunction function;
	@XmlElement(required = true)
    public XmlStepCall firstStep;
	@XmlElement(required = false)
    public List<XmlStepCall> step;
    
	@XmlElement(required = false)
    public List<XmlListenerCall> listener;
	@XmlElement(required = false, defaultValue = "false")
    public Boolean overrideListeners;

	@Override
	public Function getFunction() {
		return function;
	}

	@Override
	public StepCall getFirstStep() {
		return firstStep;
	}

	@Override
	public Optional<List<? extends StepCall>> getSteps() {
		return step == null ? Optional.empty() : Optional.of(step);
	}

	@Override
	public Optional<List<? extends ListenerCall>> getListeners() {
		return listener == null ? Optional.empty() : Optional.of(listener);
	}

	@Override
	public Optional<Boolean> getOverrideListeners() {
		return Optional.of(overrideListeners);
	}
}
