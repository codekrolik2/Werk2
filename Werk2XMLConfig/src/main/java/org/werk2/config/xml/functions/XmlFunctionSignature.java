package org.werk2.config.xml.functions;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "signature")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFunctionSignature {
	@XmlElement(required = false)
    public List<XmlFunctionParameter> inParameter;
	@XmlElement(required = false)
    public List<XmlFunctionParameter> outParameter;
}
