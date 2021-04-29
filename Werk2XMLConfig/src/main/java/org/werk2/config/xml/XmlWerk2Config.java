package org.werk2.config.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.werk2.config.xml.engine.XmlEngine;
import org.werk2.config.xml.entities.XmlExec;
import org.werk2.config.xml.entities.XmlFlow;
import org.werk2.config.xml.entities.XmlStep;
import org.werk2.config.xml.entities.XmlTransit;
import org.werk2.config.xml.functions.XmlFunction;

@XmlRootElement(name = "config")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlWerk2Config {
	@XmlElement(required = false)
    protected XmlEngine engine;
	@XmlElement(required = false)
    protected List<XmlFlow> flow;
	@XmlElement(required = false)
    protected List<XmlStep> step;
	@XmlElement(required = false)
    protected List<XmlExec> exec;
	@XmlElement(required = false)
    protected List<XmlTransit> transit;
	@XmlElement(required = false)
    protected List<XmlFunction> rawFunction;
}