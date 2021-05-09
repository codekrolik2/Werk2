package org.werk2.config.xml.entities;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.entities.ListenerCall;
import org.werk2.config.entities.Transit;
import org.werk2.config.xml.XmlDocumented;

@XmlType(name = "transit")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlTransit extends XmlDocumented implements Transit {
	@XmlElement(required = true)
	public String transitFunctionName;
	@XmlElement(required = true)
	public String rawFunctionName;
	@XmlElement(required = false)
    public List<XmlListenerCall> listener;
	@XmlElement(required = false, defaultValue = "false")
    public Boolean overrideListeners;

	@Override
	public String getTransitFunctionName() {
		return transitFunctionName;
	}

	@Override
	public String getRawFunctionName() {
		return rawFunctionName;
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
