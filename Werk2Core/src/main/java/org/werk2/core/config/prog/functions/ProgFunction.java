package org.werk2.core.config.prog.functions;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.functions.Function;
import org.werk2.config.functions.FunctionSignature;
import org.werk2.core.config.prog.ProgDocumented;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ProgFunction extends ProgDocumented implements Function {
	public ProgFunction(Optional<? extends Doc> doc, String functionName, Optional<String> physicalName,
			List<? extends FunctionSignature> signatures) {
		super(doc);
		this.functionName = functionName;
		this.physicalName = physicalName;
		this.signatures = signatures;
	}
	
	@NonNull protected String functionName;
	@NonNull protected Optional<String> physicalName;
	@NonNull protected List<? extends FunctionSignature> signatures;
}
