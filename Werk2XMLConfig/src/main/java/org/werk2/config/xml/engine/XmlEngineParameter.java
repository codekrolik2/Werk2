package org.werk2.config.xml.engine;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.engine.EngineParameter;
import org.werk2.config.xml.XmlDocumented;

@XmlType(name = "engine_prm")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlEngineParameter extends XmlDocumented implements EngineParameter {
	@XmlElement(required = true)
	public String name;
	@XmlElement(required = true)
	public String value;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}
}
