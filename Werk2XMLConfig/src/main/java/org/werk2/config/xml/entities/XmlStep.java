package org.werk2.config.xml.entities;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.Doc;
import org.werk2.config.calls.BatchCall;
import org.werk2.config.calls.Call;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.entities.Step;
import org.werk2.config.functions.Function;
import org.werk2.config.xml.XmlDocEntry;
import org.werk2.config.xml.calls.XmlBatchCall;
import org.werk2.config.xml.calls.XmlCall;
import org.werk2.config.xml.functions.XmlFunction;

@SuppressWarnings({ "unchecked", "rawtypes" })
@XmlType(name = "step")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlStep implements Step {
	@XmlElement(required = true)
    public XmlFunction function;
	@XmlElement(required = false)
    public List<XmlBatchCall> execBlock;
	@XmlElement(required = true)
    public XmlCall transit;

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
	public Optional<List<BatchCall>> getExecBlocks() {
		return execBlock == null ? Optional.empty() : Optional.of((List<BatchCall>)(List)execBlock);
	}

	@Override
	public Call getTransit() {
		return transit;
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
