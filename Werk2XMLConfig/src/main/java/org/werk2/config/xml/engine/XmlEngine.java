package org.werk2.config.xml.engine;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.Doc;
import org.werk2.config.engine.Engine;
import org.werk2.config.engine.EngineParameter;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.xml.XmlDocEntry;
import org.werk2.config.xml.entities.XmlListenerCall;

@SuppressWarnings({ "unchecked", "rawtypes" })
@XmlType(name = "engine")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlEngine implements Engine {
	@XmlElement(required = false)
    public List<XmlEngineParameter> parameter;
	@XmlElement(required = false)
    public List<XmlListenerCall> listener;

	@XmlElement(required = false)
    public XmlDocEntry doc;

	@Override
	public Optional<Doc> getDoc() {
		return doc == null ? Optional.empty() : Optional.of(doc);
	}

	@Override
	public Optional<List<EngineParameter>> getParameters() {
		return parameter == null ? Optional.empty() : Optional.of((List<EngineParameter>)(List)parameter);	
	}

	@Override
	public Optional<List<ListenerCall>> getListeners() {
		return listener == null ? Optional.empty() : Optional.of((List<ListenerCall>)(List)listener);
	}
}
