package org.werk2.config.xml.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.xml.calls.XmlCall;
import org.werk2.config.xml.functions.XmlFunction;

@XmlType(name = "flow")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFlow {
	@XmlElement(required = true)
    protected XmlFunction function;
	@XmlElement(required = true)
    protected XmlCall firstStep;
	@XmlElement(required = false)
    protected List<XmlCall> step;
    
	@XmlElement(required = false)
    protected List<XmlListenerCall> listener;
	@XmlElement(required = false, defaultValue = "false")
    protected Boolean overrideListeners;
}
