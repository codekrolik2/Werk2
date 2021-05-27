package org.werk2.core.config.prog.calls;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.calls.Call;
import org.werk2.config.calls.Concurrency;
import org.werk2.config.calls.InBinding;
import org.werk2.config.calls.OutBinding;
import org.werk2.core.config.prog.ProgDocumented;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ProgCall extends ProgDocumented implements Call {
    public ProgCall(Optional<? extends Doc> doc, String functionName,
			Optional<? extends List<? extends InBinding>> inParameters,
			Optional<? extends List<? extends OutBinding>> outParameters, Optional<String> outStatusBinding,
			Optional<String> outStatusMessageBinding, Optional<String> outTransitionStatusBinding,
			Optional<Concurrency> concurrency) {
		super(doc);
		this.functionName = functionName;
		this.inParameters = inParameters;
		this.outParameters = outParameters;
		this.outStatusBinding = outStatusBinding;
		this.outStatusMessageBinding = outStatusMessageBinding;
		this.outTransitionStatusBinding = outTransitionStatusBinding;
		this.concurrency = concurrency;
	}
    
    @NonNull protected String functionName;
    @NonNull protected Optional<? extends List<? extends InBinding>> inParameters;
    @NonNull protected Optional<? extends List<? extends OutBinding>> outParameters;
    @NonNull protected Optional<String> outStatusBinding;
    @NonNull protected Optional<String> outStatusMessageBinding;
    @NonNull protected Optional<String> outTransitionStatusBinding;
    @NonNull protected Optional<Concurrency> concurrency;
}
