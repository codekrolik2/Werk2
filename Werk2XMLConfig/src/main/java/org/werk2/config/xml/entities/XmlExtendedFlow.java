package org.werk2.config.xml.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.xml.calls.XmlCall;
import org.werk2.config.xml.functions.XmlFunctionSignature;

@XmlType(name = "extendedFlow")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlExtendedFlow {
	@XmlElement(required = true)
	protected String superStepFunctionName;
	@XmlElement(required = true)
	protected String newFunctionName;

	@XmlElement(required = false)
	protected List<XmlFunctionSignature> addSignature;
	@XmlElement(required = false, defaultValue = "false")
	protected Boolean dropOldSignatures;

	@XmlElement(required = false)
    protected XmlCall newFirstStep;
    
	@XmlElement(required = false)
    protected List<XmlCall> addStep;
	@XmlElement(required = false, defaultValue = "false")
	protected Boolean dropOldSteps;
    
	@XmlElement(required = false)
    protected List<XmlListenerCall> addListener;
	@XmlElement(required = false, defaultValue = "false")
    protected Boolean dropOldListeners;
    
	@XmlElement(required = false)
    protected Boolean newOverrideListeners;
}
