package org.werk2.core.config.prog.functions;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.functions.FunctionParameter;
import org.werk2.config.functions.FunctionSignature;
import org.werk2.core.config.prog.ProgDocumented;

import lombok.Getter;

@Getter
public class ProgFunctionSignature extends ProgDocumented implements FunctionSignature {
    public ProgFunctionSignature(Optional<? extends Doc> doc,
			Optional<? extends List<? extends FunctionParameter>> parameters) {
		super(doc);
		this.parameters = parameters;
	}

	protected Optional<? extends List<? extends FunctionParameter>> parameters;
}
