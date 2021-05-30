package org.werk2.config.xml.entities;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.Doc;
import org.werk2.config.calls.BatchCall;
import org.werk2.config.calls.Call;
import org.werk2.config.entities.ExtendedStep;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.functions.FunctionSignature;
import org.werk2.config.xml.XmlDocEntry;
import org.werk2.config.xml.XmlDocumented;
import org.werk2.config.xml.calls.XmlBatchCall;
import org.werk2.config.xml.calls.XmlCall;
import org.werk2.config.xml.functions.XmlFunctionSignature;

@XmlType(name = "extendedStep")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlExtendedStep extends XmlDocumented implements ExtendedStep {
	@XmlElement(required = false)
    public XmlDocEntry functionDoc;

	@XmlElement(required = true)
	public String superStepFunctionName;
	@XmlElement(required = true)
	public String newFunctionName;
	
	@XmlElement(required = false)
	public List<XmlFunctionSignature> addSignature;
	@XmlElement(required = false, defaultValue = "false")
	public Boolean dropOldSignatures;
	
	@XmlElement(required = false)
    public XmlBatchCall addExecBlock;
	@XmlElement(required = false, defaultValue = "false")
	public Boolean dropOldExec;

	@XmlElement(required = false)
    public XmlCall newTransit;

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
	public String getSuperStepFunctionName() {
		return superStepFunctionName;
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
	public Optional<BatchCall> getAddExecBlock() {
		return addExecBlock == null ? Optional.empty() : Optional.of(addExecBlock);
	}

	@Override
	public Optional<Boolean> getDropOldExecBlock() {
		return Optional.of(dropOldExec);
	}

	@Override
	public Optional<Call> getNewTransit() {
		return newTransit == null ? Optional.empty() : Optional.of(newTransit);
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
