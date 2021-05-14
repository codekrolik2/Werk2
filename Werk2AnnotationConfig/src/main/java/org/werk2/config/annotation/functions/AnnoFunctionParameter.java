package org.werk2.config.annotation.functions;

import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.functions.FunctionParameter;
import org.werk2.config.functions.ParameterDirection;
import org.werk2.config.functions.ParameterPassing;
import org.werk2.config.functions.WerkParameterType;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class AnnoFunctionParameter implements FunctionParameter {
	@NonNull protected String name;
	@NonNull protected ParameterDirection direction;
	@NonNull protected WerkParameterType type;
	@NonNull protected Optional<String> runtimeType;
	@NonNull protected Optional<ParameterPassing> pass;
	@NonNull protected Optional<? extends Doc> doc;

	public Optional<? extends Doc> getDoc() {
		return doc;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ParameterDirection getDirection() {
		return direction;
	}

	@Override
	public WerkParameterType getType() {
		return type;
	}

	@Override
	public Optional<String> getRuntimeType() {
		return runtimeType;
	}

	@Override
	public Optional<ParameterPassing> getPassing() {
		return pass;
	}
}
