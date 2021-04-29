package org.werk2.config.xml.calls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "out")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlOutBinding {
	@XmlElement(required = true)
    public String fromOutParameter;
	@XmlElement(required = true)
    public String toField;
}
