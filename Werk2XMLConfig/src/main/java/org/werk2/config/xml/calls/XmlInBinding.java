package org.werk2.config.xml.calls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "in")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlInBinding {
	@XmlElement(required = true)
	public String fromField;
	@XmlElement(required = true)
	public String toInParameter;
	@XmlElement(required = false)
	public String constValue;
}
