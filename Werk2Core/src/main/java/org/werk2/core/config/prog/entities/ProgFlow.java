package org.werk2.core.config.prog.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.entities.Flow;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.entities.StepCall;
import org.werk2.config.functions.Function;
import org.werk2.core.config.prog.ProgDocumented;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ProgFlow extends ProgDocumented implements Flow {
    public ProgFlow(Optional<? extends Doc> doc, Function function, StepCall firstStep,
			Optional<? extends List<? extends StepCall>> steps,
			Optional<? extends List<? extends ListenerCall>> listeners, Optional<Boolean> overrideListeners) {
		super(doc);
		this.function = function;
		this.firstStep = firstStep;
		this.steps = steps;
		this.listeners = listeners;
		this.overrideListeners = overrideListeners;
	}
    
    @NonNull protected Function function;
    @NonNull protected StepCall firstStep;
    @NonNull protected Optional<? extends List<? extends StepCall>> steps;
    @NonNull protected Optional<? extends List<? extends ListenerCall>> listeners;
    @NonNull protected Optional<Boolean> overrideListeners;
}
