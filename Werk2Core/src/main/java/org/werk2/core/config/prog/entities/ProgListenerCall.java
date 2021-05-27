package org.werk2.core.config.prog.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.calls.BatchCall;
import org.werk2.config.calls.Call;
import org.werk2.config.calls.Concurrency;
import org.werk2.config.entities.Event;
import org.werk2.config.entities.ListenerCall;
import org.werk2.core.config.prog.calls.ProgBatchCall;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ProgListenerCall extends ProgBatchCall implements ListenerCall {
    public ProgListenerCall(Optional<? extends Doc> doc, Optional<? extends List<? extends Call>> calls,
			Optional<? extends List<? extends BatchCall>> batches, Optional<Concurrency> concurrency,
			List<? extends Event> events, Optional<String> functionNameRegex) {
		super(doc, calls, batches, concurrency);
		this.events = events;
		this.functionNameRegex = functionNameRegex;
	}

    @NonNull protected List<? extends Event> events;
    @NonNull protected Optional<String> functionNameRegex;
}
