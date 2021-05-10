package org.werk2.experiment2.invoke;

import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.calls.InBinding;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
public class InPrm implements InBinding {
	protected @NonNull String fromField;
	protected @NonNull String toInParameter;
	protected String value;
	protected Doc doc;
	
	@Override
	public String fromField() {
		return fromField;
	}

	@Override
	public String toInParameter() {
		return toInParameter;
	}

	@Override
	public Optional<String> getValue() {
		return value == null ? Optional.empty() : Optional.of(value);
	}

	@Override
	public Optional<? extends Doc> getDoc() {
		return doc == null ? Optional.empty() : Optional.of(doc);
	}
}

