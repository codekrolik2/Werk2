package org.werk2.config.xml.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "exec")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlExec {
	@XmlElement(required = true)
	public String execFunctionName;
	@XmlElement(required = true)
	public String rawFunctionName;
	@XmlElement(required = false)
    public List<XmlListenerCall> listener;
	@XmlElement(required = false, defaultValue = "false")
    public Boolean overrideListeners;
}
