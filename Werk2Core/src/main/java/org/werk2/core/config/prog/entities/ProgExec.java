package org.werk2.core.config.prog.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.entities.Exec;
import org.werk2.config.entities.ListenerCall;
import org.werk2.core.config.prog.ProgDocumented;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ProgExec extends ProgDocumented implements Exec {
	public ProgExec(Optional<? extends Doc> doc, String execFunctionName, String calledFunctionName,
			Optional<? extends List<? extends ListenerCall>> listeners, Optional<Boolean> overrideListeners) {
		super(doc);
		this.execFunctionName = execFunctionName;
		this.calledFunctionName = calledFunctionName;
		this.listeners = listeners;
		this.overrideListeners = overrideListeners;
	}
	
	@NonNull protected String execFunctionName;
	@NonNull protected String calledFunctionName;
	@NonNull protected Optional<? extends List<? extends ListenerCall>> listeners;
	@NonNull protected Optional<Boolean> overrideListeners;
}
