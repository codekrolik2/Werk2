package org.werk2.config.xml.engine;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "engine_prm")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlEngineParameter {
	@XmlElement(required = true)
	public String name;
	@XmlElement(required = true)
	public String value;
}
