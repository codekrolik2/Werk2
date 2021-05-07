package org.werk2.config.xml.entities;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.Doc;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.entities.Transit;
import org.werk2.config.xml.XmlDocEntry;

@SuppressWarnings({ "unchecked", "rawtypes" })
@XmlType(name = "transit")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlTransit implements Transit {
	@XmlElement(required = true)
	public String transitFunctionName;
	@XmlElement(required = true)
	public String rawFunctionName;
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
	public String getTransitFunctionName() {
		return transitFunctionName;
	}

	@Override
	public String getRawFunctionName() {
		return rawFunctionName;
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
