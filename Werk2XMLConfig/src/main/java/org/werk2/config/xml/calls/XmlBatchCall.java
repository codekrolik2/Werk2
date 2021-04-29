package org.werk2.config.xml.calls;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "batchCall")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlBatchCall {
	@XmlElement(required = false)
    protected List<XmlCall> call;
	@XmlElement(required = false)
    protected List<XmlBatchCall> batch;
}
