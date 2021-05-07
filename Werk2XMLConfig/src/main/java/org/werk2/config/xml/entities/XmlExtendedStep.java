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
import org.werk2.config.xml.calls.XmlBatchCall;
import org.werk2.config.xml.calls.XmlCall;
import org.werk2.config.xml.functions.XmlFunctionSignature;

@SuppressWarnings({ "unchecked", "rawtypes" })
@XmlType(name = "extendedStep")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlExtendedStep implements ExtendedStep {
	@XmlElement(required = true)
	public String superStepFunctionName;
	@XmlElement(required = true)
	public String newFunctionName;
	
	@XmlElement(required = false)
	public List<XmlFunctionSignature> addSignature;
	@XmlElement(required = false, defaultValue = "false")
	public Boolean dropOldSignatures;
	
	@XmlElement(required = false)
    public List<XmlBatchCall> addExecBlock;
	@XmlElement(required = false, defaultValue = "false")
	public Boolean dropOldExecs;

	@XmlElement(required = false)
    public XmlCall newTransit;

	@XmlElement(required = false)
    public List<XmlListenerCall> addListener;
	@XmlElement(required = false, defaultValue = "false")
    public Boolean dropOldListeners;
    
	@XmlElement(required = false)
    public Boolean newOverrideListeners;

	@XmlElement(required = false)
    public XmlDocEntry doc;

	@Override
	public Optional<Doc> getDoc() {
		return doc == null ? Optional.empty() : Optional.of(doc);
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
	public Optional<List<FunctionSignature>> getAddSignatures() {
		return addSignature == null ? Optional.empty() : Optional.of((List<FunctionSignature>)(List)addSignature);
	}

	@Override
	public Optional<Boolean> getDropOldSignatures() {
		return Optional.of(dropOldSignatures);
	}

	@Override
	public Optional<List<BatchCall>> getAddExecBlocks() {
		return addExecBlock == null ? Optional.empty() : Optional.of((List<BatchCall>)(List)addExecBlock);
	}

	@Override
	public Optional<Boolean> getDropOldExecBlocks() {
		return Optional.of(dropOldExecs);
	}

	@Override
	public Optional<Call> getNewTransit() {
		return newTransit == null ? Optional.empty() : Optional.of(newTransit);
	}

	@Override
	public Optional<List<ListenerCall>> getAddListeners() {
		return addListener == null ? Optional.empty() : Optional.of((List<ListenerCall>)(List)addListener);
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
