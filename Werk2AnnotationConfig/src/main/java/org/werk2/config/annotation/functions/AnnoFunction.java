package org.werk2.config.annotation.functions;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.functions.Function;
import org.werk2.config.functions.FunctionSignature;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class AnnoFunction implements Function {
	@NonNull protected String functionName;
	@NonNull protected Optional<String> physicalName;
	@NonNull protected List<? extends FunctionSignature> signatures;
	@NonNull protected Optional<? extends Doc> doc;

	public Optional<? extends Doc> getDoc() {
		return doc;
	}

	public String getFunctionName() {
		return functionName;
	}

	public List<? extends FunctionSignature> getSignatures() {
		return signatures;
	}

	public Optional<String> getPhysicalName() {
		return physicalName;
	}
}
