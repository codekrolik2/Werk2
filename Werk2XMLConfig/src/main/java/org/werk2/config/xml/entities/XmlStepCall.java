package org.werk2.config.xml.entities;

import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.entities.StepCall;
import org.werk2.config.xml.calls.XmlCall;

@XmlType(name = "stepCall")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlStepCall extends XmlCall implements StepCall {
	@XmlElement(required = false)
    public String stepAlias;

	@Override
	public Optional<String> getStepAlias() {
		return stepAlias == null ? Optional.empty() : Optional.of(stepAlias);
	}
}
