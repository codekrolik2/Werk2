package org.werk2.config.xml.functions;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "function")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFunction {
	@XmlElement(required = true)
	protected String functionName;
	@XmlElement(required = true)
	protected List<XmlFunctionSignature> signature;
}
