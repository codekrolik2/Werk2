package org.werk2.config.xml.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.xml.calls.XmlBatchCall;

@XmlType(name = "listener")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlListenerCall extends XmlBatchCall {
	@XmlElement(required = true)
    public List<XmlEvent> event;
	@XmlElement(required = false)
    public String functionNameRegex;
}
