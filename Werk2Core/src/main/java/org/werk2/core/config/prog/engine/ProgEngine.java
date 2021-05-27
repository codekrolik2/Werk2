package org.werk2.core.config.prog.engine;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.engine.Engine;
import org.werk2.config.engine.EngineParameter;
import org.werk2.config.entities.ListenerCall;
import org.werk2.core.config.prog.ProgDocumented;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ProgEngine extends ProgDocumented implements Engine {
    public ProgEngine(Optional<? extends Doc> doc, Optional<? extends List<? extends EngineParameter>> parameters,
			Optional<? extends List<? extends ListenerCall>> listeners) {
		super(doc);
		this.parameters = parameters;
		this.listeners = listeners;
	}
    
    @NonNull protected Optional<? extends List<? extends EngineParameter>> parameters;
    @NonNull protected Optional<? extends List<? extends ListenerCall>> listeners;
}
