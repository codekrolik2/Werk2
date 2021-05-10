package org.werk2.config.xml.calls;

import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.calls.InBinding;
import org.werk2.config.xml.XmlDocumented;

@XmlType(name = "in")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlInBinding extends XmlDocumented implements InBinding {
	@XmlElement(required = true)
	public String fromField;
	@XmlElement(required = true)
	public String toInParameter;
	@XmlElement(required = false)
	public String value;

	@Override
	public String fromField() {
		return fromField;
	}

	@Override
	public String toInParameter() {
		return toInParameter;
	}

	@Override
	public Optional<String> getValue() {
		return value == null ? Optional.empty() : Optional.of(value);	
	}
}
