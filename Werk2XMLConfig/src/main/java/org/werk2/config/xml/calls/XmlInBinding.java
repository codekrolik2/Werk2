package org.werk2.config.xml.calls;

import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.Doc;
import org.werk2.config.calls.InBinding;
import org.werk2.config.xml.XmlDocEntry;

@XmlType(name = "in")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlInBinding implements InBinding {
	@XmlElement(required = true)
	public String fromField;
	@XmlElement(required = true)
	public String toInParameter;
	@XmlElement(required = false)
	public String value;

	@XmlElement(required = false)
    public XmlDocEntry doc;

	@Override
	public Optional<Doc> getDoc() {
		return doc == null ? Optional.empty() : Optional.of(doc);
	}

	@Override
	public Optional<String> getFromField() {
		return fromField == null ? Optional.empty() : Optional.of(fromField);
	}

	@Override
	public String getToInParameter() {
		return toInParameter;
	}

	@Override
	public Optional<String> getValue() {
		return value == null ? Optional.empty() : Optional.of(value);	
	}
}
