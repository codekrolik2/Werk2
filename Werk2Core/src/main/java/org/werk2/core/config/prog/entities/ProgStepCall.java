package org.werk2.core.config.prog.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.calls.Concurrency;
import org.werk2.config.calls.InBinding;
import org.werk2.config.calls.OutBinding;
import org.werk2.config.entities.StepCall;
import org.werk2.core.config.prog.calls.ProgCall;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ProgStepCall extends ProgCall implements StepCall {
    public ProgStepCall(Optional<? extends Doc> doc, String functionName,
			Optional<? extends List<? extends InBinding>> inParameters,
			Optional<? extends List<? extends OutBinding>> outParameters, Optional<String> outStatusBinding,
			Optional<String> outStatusMessageBinding, Optional<String> outTransitionStatusBinding,
			Optional<Concurrency> concurrency, Optional<String> stepAlias) {
		super(doc, functionName, inParameters, outParameters, outStatusBinding, outStatusMessageBinding,
				outTransitionStatusBinding, concurrency);
		this.stepAlias = stepAlias;
	}

    @NonNull protected Optional<String> stepAlias;
}
