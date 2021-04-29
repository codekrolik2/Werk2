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
	public String superStepFunctionName;
	@XmlElement(required = true)
	public String newFunctionName;

	@XmlElement(required = false)
	public List<XmlFunctionSignature> addSignature;
	@XmlElement(required = false, defaultValue = "false")
	public Boolean dropOldSignatures;

	@XmlElement(required = false)
    public XmlCall newFirstStep;
    
	@XmlElement(required = false)
    public List<XmlCall> addStep;
	@XmlElement(required = false, defaultValue = "false")
	public Boolean dropOldSteps;
    
	@XmlElement(required = false)
    public List<XmlListenerCall> addListener;
	@XmlElement(required = false, defaultValue = "false")
    public Boolean dropOldListeners;
    
	@XmlElement(required = false)
    public Boolean newOverrideListeners;
}
