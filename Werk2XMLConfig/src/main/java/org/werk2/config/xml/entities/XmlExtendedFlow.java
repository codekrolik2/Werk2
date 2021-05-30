package org.werk2.config.xml.entities;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.Doc;
import org.werk2.config.entities.ExtendedFlow;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.entities.StepCall;
import org.werk2.config.functions.FunctionSignature;
import org.werk2.config.xml.XmlDocEntry;
import org.werk2.config.xml.XmlDocumented;
import org.werk2.config.xml.functions.XmlFunctionSignature;

@XmlType(name = "extendedFlow")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlExtendedFlow extends XmlDocumented implements ExtendedFlow {
	@XmlElement(required = false)
    public XmlDocEntry functionDoc;

	@XmlElement(required = true)
	public String superFlowFunctionName;
	@XmlElement(required = true)
	public String newFunctionName;

	@XmlElement(required = false)
	public List<XmlFunctionSignature> addSignature;
	@XmlElement(required = false, defaultValue = "false")
	public Boolean dropOldSignatures;

	@XmlElement(required = false)
    public XmlStepCall newFirstStep;
    
	@XmlElement(required = false)
    public List<XmlStepCall> addStep;
	@XmlElement(required = false, defaultValue = "false")
	public Boolean dropOldSteps;
    
	@XmlElement(required = false)
    public List<XmlListenerCall> addListener;
	@XmlElement(required = false, defaultValue = "false")
    public Boolean dropOldListeners;
    
	@XmlElement(required = false)
    public Boolean newOverrideListeners;

	@Override
	public Optional<? extends Doc> getFunctionDoc() {
		return functionDoc == null ? Optional.empty() : Optional.of(functionDoc);
	}

	@Override
	public String getSuperFlowFunctionName() {
		return superFlowFunctionName;
	}

	@Override
	public String getNewFunctionName() {
		return newFunctionName;
	}

	@Override
	public Optional<List<? extends FunctionSignature>> getAddSignatures() {
		return addSignature == null ? Optional.empty() : Optional.of(addSignature);
	}

	@Override
	public Optional<Boolean> getDropOldSignatures() {
		return Optional.of(dropOldSignatures);
	}

	@Override
	public Optional<StepCall> getNewFirstStep() {
		return newFirstStep == null ? Optional.empty() : Optional.of(newFirstStep);
	}

	@Override
	public Optional<List<? extends StepCall>> getAddSteps() {
		return addStep == null ? Optional.empty() : Optional.of(addStep);
	}

	@Override
	public Optional<Boolean> getDropOldSteps() {
		return dropOldSteps == null ? Optional.empty() : Optional.of(dropOldSteps);
	}

	@Override
	public Optional<List<? extends ListenerCall>> getAddListeners() {
		return addListener == null ? Optional.empty() : Optional.of(addListener);
	}

	@Override
	public Optional<Boolean> getDropOldListeners() {
		return Optional.of(dropOldListeners);
	}

	@Override
	public Optional<Boolean> getNewOverrideListeners() {
		return newOverrideListeners == null ? Optional.empty() : Optional.of(newOverrideListeners);
	}
}
