package org.werk2.config.xml.calls;

import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.Doc;
import org.werk2.config.calls.OutBinding;
import org.werk2.config.xml.XmlDocEntry;

@XmlType(name = "out")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlOutBinding implements OutBinding {
	@XmlElement(required = true)
    public String fromOutParameter;
	@XmlElement(required = true)
    public String toField;

	@XmlElement(required = false)
    public XmlDocEntry doc;

	@Override
	public Optional<Doc> getDoc() {
		return doc == null ? Optional.empty() : Optional.of(doc);
	}

	@Override
	public String getFromOutParameter() {
		return fromOutParameter;
	}

	@Override
	public String getToField() {
		return toField;
	}
}
