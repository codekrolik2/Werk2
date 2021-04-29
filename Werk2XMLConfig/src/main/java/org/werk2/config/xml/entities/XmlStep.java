package org.werk2.config.xml.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.xml.calls.XmlBatchCall;
import org.werk2.config.xml.calls.XmlCall;
import org.werk2.config.xml.functions.XmlFunction;

@XmlType(name = "step")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlStep {
	@XmlElement(required = true)
    protected XmlFunction function;
	@XmlElement(required = false)
    protected List<XmlBatchCall> execBlock;
	@XmlElement(required = true)
    protected XmlCall transit;

	@XmlElement(required = false)
    protected List<XmlListenerCall> listener;
	@XmlElement(required = false, defaultValue = "false")
    protected Boolean overrideListeners;
}
