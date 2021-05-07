package org.werk2.config.xml.entities;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.Doc;
import org.werk2.config.calls.Call;
import org.werk2.config.entities.Flow;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.functions.Function;
import org.werk2.config.xml.XmlDocEntry;
import org.werk2.config.xml.calls.XmlCall;
import org.werk2.config.xml.functions.XmlFunction;

@SuppressWarnings({ "unchecked", "rawtypes" })
@XmlType(name = "flow")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFlow implements Flow {
	@XmlElement(required = true)
    public XmlFunction function;
	@XmlElement(required = true)
    public XmlCall firstStep;
	@XmlElement(required = false)
    public List<XmlCall> step;
    
	@XmlElement(required = false)
    public List<XmlListenerCall> listener;
	@XmlElement(required = false, defaultValue = "false")
    public Boolean overrideListeners;

	@XmlElement(required = false)
    public XmlDocEntry doc;

	@Override
	public Optional<Doc> getDoc() {
		return doc == null ? Optional.empty() : Optional.of(doc);
	}

	@Override
	public Function getFunction() {
		return function;
	}

	@Override
	public Call getFirstStep() {
		return firstStep;
	}

	@Override
	public Optional<List<Call>> getSteps() {
		return step == null ? Optional.empty() : Optional.of((List<Call>)(List)step);
	}

	@Override
	public Optional<List<ListenerCall>> getListeners() {
		return listener == null ? Optional.empty() : Optional.of((List<ListenerCall>)(List)listener);
	}

	@Override
	public Optional<Boolean> getOverrideListeners() {
		return Optional.of(overrideListeners);
	}
}
