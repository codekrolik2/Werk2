package org.werk2.core.config.prog.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.calls.BatchCall;
import org.werk2.config.calls.Call;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.entities.Step;
import org.werk2.config.functions.Function;
import org.werk2.core.config.prog.ProgDocumented;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ProgStep extends ProgDocumented implements Step {
    public ProgStep(Optional<? extends Doc> doc, Function function, Optional<BatchCall> execBlock, Call transit,
			Optional<? extends List<? extends ListenerCall>> listeners, Optional<Boolean> overrideListeners) {
		super(doc);
		this.function = function;
		this.execBlock = execBlock;
		this.transit = transit;
		this.listeners = listeners;
		this.overrideListeners = overrideListeners;
	}
    
    @NonNull protected Function function;
    @NonNull protected Optional<BatchCall> execBlock;
    @NonNull protected Call transit;
    @NonNull protected Optional<? extends List<? extends ListenerCall>> listeners;
    @NonNull protected Optional<Boolean> overrideListeners;
}
