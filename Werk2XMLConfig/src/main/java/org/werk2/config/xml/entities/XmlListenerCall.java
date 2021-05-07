package org.werk2.config.xml.entities;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.entities.Event;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.xml.calls.XmlBatchCall;

@SuppressWarnings({ "unchecked", "rawtypes" })
@XmlType(name = "listener")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlListenerCall extends XmlBatchCall implements ListenerCall {
	@XmlElement(required = true)
    public List<XmlEvent> event;
	@XmlElement(required = false)
    public String functionNameRegex;
	
	@Override
	public List<Event> getEvents() {
		return (List<Event>)(List)event;
	}
	
	@Override
	public Optional<String> getFunctionNameRegex() {
		return functionNameRegex == null ? Optional.empty() : Optional.of(functionNameRegex);
	}
}
