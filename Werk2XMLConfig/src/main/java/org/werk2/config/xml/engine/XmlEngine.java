package org.werk2.config.xml.engine;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.engine.Engine;
import org.werk2.config.engine.EngineParameter;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.xml.XmlDocumented;
import org.werk2.config.xml.entities.XmlListenerCall;

@XmlType(name = "engine")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlEngine extends XmlDocumented implements Engine {
	@XmlElement(required = false)
    public List<XmlEngineParameter> parameter;
	@XmlElement(required = false)
    public List<XmlListenerCall> listener;

	@Override
	public Optional<List<? extends EngineParameter>> getParameters() {
		return parameter == null ? Optional.empty() : Optional.of(parameter);	
	}

	@Override
	public Optional<List<? extends ListenerCall>> getListeners() {
		return listener == null ? Optional.empty() : Optional.of(listener);
	}
}
