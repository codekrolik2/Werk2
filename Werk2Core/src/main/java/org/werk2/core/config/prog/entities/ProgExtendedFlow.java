package org.werk2.core.config.prog.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.entities.ExtendedFlow;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.entities.StepCall;
import org.werk2.config.functions.FunctionSignature;
import org.werk2.core.config.prog.ProgDocumented;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ProgExtendedFlow extends ProgDocumented implements ExtendedFlow {
	public ProgExtendedFlow(Optional<? extends Doc> doc, String superFlowFunctionName, String newFunctionName,
			Optional<? extends List<? extends FunctionSignature>> addSignatures, Optional<Boolean> dropOldSignatures,
			Optional<? extends StepCall> newFirstStep, Optional<? extends List<? extends StepCall>> addSteps,
			Optional<Boolean> dropOldSteps, Optional<? extends List<? extends ListenerCall>> addListeners,
			Optional<Boolean> dropOldListeners, Optional<Boolean> newOverrideListeners) {
		super(doc);
		this.superFlowFunctionName = superFlowFunctionName;
		this.newFunctionName = newFunctionName;
		this.addSignatures = addSignatures;
		this.dropOldSignatures = dropOldSignatures;
		this.newFirstStep = newFirstStep;
		this.addSteps = addSteps;
		this.dropOldSteps = dropOldSteps;
		this.addListeners = addListeners;
		this.dropOldListeners = dropOldListeners;
		this.newOverrideListeners = newOverrideListeners;
	}
	
	@NonNull protected String superFlowFunctionName;
	@NonNull protected String newFunctionName;
	@NonNull protected Optional<? extends List<? extends FunctionSignature>> addSignatures;
	@NonNull protected Optional<Boolean> dropOldSignatures;
	@NonNull protected Optional<? extends StepCall> newFirstStep;
	@NonNull protected Optional<? extends List<? extends StepCall>> addSteps;
	@NonNull protected Optional<Boolean> dropOldSteps;
	@NonNull protected Optional<? extends List<? extends ListenerCall>> addListeners;
	@NonNull protected Optional<Boolean> dropOldListeners;
	@NonNull protected Optional<Boolean> newOverrideListeners;
}
