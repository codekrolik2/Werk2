package org.werk2.config.xml.engine;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.xml.entities.XmlListenerCall;

@XmlType(name = "engine")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlEngine {
	@XmlElement(required = false)
    public List<XmlEngineParameter> parameter;
	@XmlElement(required = false)
    public List<XmlListenerCall> listener;
}
