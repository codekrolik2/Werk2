package org.werk2.core.config.prog.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.calls.BatchCall;
import org.werk2.config.calls.Call;
import org.werk2.config.entities.ExtendedStep;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.functions.FunctionSignature;
import org.werk2.core.config.prog.ProgDocumented;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ProgExtendedStep extends ProgDocumented implements ExtendedStep {
	public ProgExtendedStep(Optional<? extends Doc> doc, String superStepFunctionName, String newFunctionName,
			Optional<? extends List<? extends FunctionSignature>> addSignatures, Optional<Boolean> dropOldSignatures,
			Optional<BatchCall> addExecBlock, Optional<Boolean> dropOldExecBlock, Optional<? extends Call> newTransit,
			Optional<? extends List<? extends ListenerCall>> addListeners, Optional<Boolean> dropOldListeners,
			Optional<Boolean> newOverrideListeners) {
		super(doc);
		this.superStepFunctionName = superStepFunctionName;
		this.newFunctionName = newFunctionName;
		this.addSignatures = addSignatures;
		this.dropOldSignatures = dropOldSignatures;
		this.addExecBlock = addExecBlock;
		this.dropOldExecBlock = dropOldExecBlock;
		this.newTransit = newTransit;
		this.addListeners = addListeners;
		this.dropOldListeners = dropOldListeners;
		this.newOverrideListeners = newOverrideListeners;
	}
	
	@NonNull protected String superStepFunctionName;
	@NonNull protected String newFunctionName;
	@NonNull protected Optional<? extends List<? extends FunctionSignature>> addSignatures;
	@NonNull protected Optional<Boolean> dropOldSignatures;
	@NonNull protected Optional<BatchCall> addExecBlock;
	@NonNull protected Optional<Boolean> dropOldExecBlock;
	@NonNull protected Optional<? extends Call> newTransit;
	@NonNull protected Optional<? extends List<? extends ListenerCall>> addListeners;
	@NonNull protected Optional<Boolean> dropOldListeners;
	@NonNull protected Optional<Boolean> newOverrideListeners;
}
