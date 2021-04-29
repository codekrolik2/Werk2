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
    protected String functionName;
	@XmlElement(required = false)
    protected List<XmlInBinding> inParameter;
	@XmlElement(required = false)
    protected List<XmlOutBinding> outParameter;
	@XmlElement(required = false)
    protected String outStatus;
	@XmlElement(required = false)
    protected String outStatusMessage;
	@XmlElement(required = false)
    protected String outTransitionStatus;
	@XmlElement(required = false, defaultValue = "SYNCHRONIZED")
    protected XmlConcurrency concurrency;
}
