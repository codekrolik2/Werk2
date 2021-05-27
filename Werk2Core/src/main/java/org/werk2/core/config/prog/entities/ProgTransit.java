package org.werk2.core.config.prog.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.entities.ListenerCall;
import org.werk2.config.entities.Transit;
import org.werk2.core.config.prog.ProgDocumented;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ProgTransit extends ProgDocumented implements Transit {
	public ProgTransit(Optional<? extends Doc> doc, String transitFunctionName, String calledFunctionName,
			Optional<? extends List<? extends ListenerCall>> listeners, Optional<Boolean> overrideListeners) {
		super(doc);
		this.transitFunctionName = transitFunctionName;
		this.calledFunctionName = calledFunctionName;
		this.listeners = listeners;
		this.overrideListeners = overrideListeners;
	}
	
	@NonNull protected String transitFunctionName;
	@NonNull protected String calledFunctionName;
	@NonNull protected Optional<? extends List<? extends ListenerCall>> listeners;
	@NonNull protected Optional<Boolean> overrideListeners;
}
