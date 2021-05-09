package org.werk2.config.xml.calls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.calls.OutBinding;
import org.werk2.config.xml.XmlDocumented;

@XmlType(name = "out")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlOutBinding extends XmlDocumented implements OutBinding {
	@XmlElement(required = true)
    public String fromOutParameter;
	@XmlElement(required = true)
    public String toField;

	@Override
	public String getFromOutParameter() {
		return fromOutParameter;
	}

	@Override
	public String getToField() {
		return toField;
	}
}
