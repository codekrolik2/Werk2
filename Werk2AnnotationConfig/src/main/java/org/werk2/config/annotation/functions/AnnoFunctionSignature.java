package org.werk2.config.annotation.functions;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.functions.FunctionParameter;
import org.werk2.config.functions.FunctionSignature;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class AnnoFunctionSignature implements FunctionSignature {
	@NonNull protected Optional<List<? extends FunctionParameter>> parameters;
	@NonNull protected Optional<? extends Doc> doc;

	public Optional<? extends Doc> getDoc() {
		return doc;
	}

	@Override
	public Optional<List<? extends FunctionParameter>> getParameters() {
		return parameters;
	}
}
