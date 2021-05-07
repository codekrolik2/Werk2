package org.werk2.config.xml.engine;

import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.Doc;
import org.werk2.config.engine.EngineParameter;
import org.werk2.config.xml.XmlDocEntry;

@XmlType(name = "engine_prm")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlEngineParameter implements EngineParameter {
	@XmlElement(required = true)
	public String name;
	@XmlElement(required = true)
	public String value;

	@XmlElement(required = false)
    public XmlDocEntry doc;

	@Override
	public Optional<Doc> getDoc() {
		return doc == null ? Optional.empty() : Optional.of(doc);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}
}
