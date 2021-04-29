package org.werk2.config.xml.calls;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "call")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlCall {
	@XmlElement(required = true)
    public String functionName;
	@XmlElement(required = false)
    public List<XmlInBinding> inParameter;
	@XmlElement(required = false)
    public List<XmlOutBinding> outParameter;
	@XmlElement(required = false)
    public String outStatus;
	@XmlElement(required = false)
    public String outStatusMessage;
	@XmlElement(required = false)
    public String outTransitionStatus;
	@XmlElement(required = false, defaultValue = "SYNCHRONIZED")
    public XmlConcurrency concurrency;
}
