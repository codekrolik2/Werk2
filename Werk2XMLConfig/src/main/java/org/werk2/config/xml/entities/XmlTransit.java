package org.werk2.config.xml.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "transit")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlTransit {
	@XmlElement(required = true)
	protected String transitFunctionName;
	@XmlElement(required = true)
	protected String rawFunctionName;
	@XmlElement(required = false)
    protected List<XmlListenerCall> listener;
	@XmlElement(required = false, defaultValue = "false")
    protected Boolean overrideListeners;
}
