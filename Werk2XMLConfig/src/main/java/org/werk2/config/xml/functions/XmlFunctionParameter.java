package org.werk2.config.xml.functions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "prm")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFunctionParameter {
	@XmlElement(required = true)
	protected String name;
	@XmlElement(required = true)
	protected TypeXml type;
	@XmlElement(required = false)
	protected String runtimeType;
	@XmlElement(required = false, defaultValue = "BY_REF")
	protected XmlParameterPassing pass;
}
