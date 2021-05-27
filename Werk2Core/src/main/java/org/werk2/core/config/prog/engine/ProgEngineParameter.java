package org.werk2.core.config.prog.engine;

import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.engine.EngineParameter;
import org.werk2.core.config.prog.ProgDocumented;

import lombok.Getter;

@Getter
public class ProgEngineParameter extends ProgDocumented implements EngineParameter {
	public ProgEngineParameter(Optional<? extends Doc> doc, String name, String value) {
		super(doc);
		this.name = name;
		this.value = value;
	}
	
	protected String name;
	protected String value;
}
