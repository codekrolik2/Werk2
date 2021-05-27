package org.werk2.core.config.prog.functions;

import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.functions.FunctionParameter;
import org.werk2.config.functions.ParameterDirection;
import org.werk2.config.functions.ParameterPassing;
import org.werk2.config.functions.WerkParameterType;
import org.werk2.core.config.prog.ProgDocumented;

import lombok.Getter;

@Getter
public class ProgFunctionParameter extends ProgDocumented implements FunctionParameter {
	public ProgFunctionParameter(Optional<? extends Doc> doc, String name, ParameterDirection direction,
			WerkParameterType type, Optional<String> runtimeType, Optional<ParameterPassing> passing) {
		super(doc);
		this.name = name;
		this.direction = direction;
		this.type = type;
		this.runtimeType = runtimeType;
		this.passing = passing;
	}
	
	protected String name;
	protected ParameterDirection direction;
	protected WerkParameterType type;
	protected Optional<String> runtimeType;
	protected Optional<ParameterPassing> passing;
}
