package org.werk2.config.xml.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.xml.calls.XmlBatchCall;
import org.werk2.config.xml.calls.XmlCall;
import org.werk2.config.xml.functions.XmlFunctionSignature;

@XmlType(name = "extendedStep")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlExtendedStep {
	@XmlElement(required = true)
	protected String superStepFunctionName;
	@XmlElement(required = true)
	protected String newFunctionName;
	
	@XmlElement(required = false)
	protected List<XmlFunctionSignature> addSignature;
	@XmlElement(required = false, defaultValue = "false")
	protected Boolean dropOldSignatures;
	
	@XmlElement(required = false)
    protected List<XmlBatchCall> addExecBlock;
	@XmlElement(required = false, defaultValue = "false")
	protected Boolean dropOldExecs;

	@XmlElement(required = false)
    protected XmlCall newTransit;

	@XmlElement(required = false)
    protected List<XmlListenerCall> addListener;
	@XmlElement(required = false, defaultValue = "false")
    protected Boolean dropOldListeners;
    
	@XmlElement(required = false)
    protected Boolean newOverrideListeners;
}
