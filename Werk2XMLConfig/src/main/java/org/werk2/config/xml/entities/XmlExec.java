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
	protected String execFunctionName;
	@XmlElement(required = true)
	protected String rawFunctionName;
	@XmlElement(required = false)
    protected List<XmlListenerCall> listener;
	@XmlElement(required = false, defaultValue = "false")
    protected Boolean overrideListeners;
}
