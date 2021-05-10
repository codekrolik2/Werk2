package org.werk2.experiment2.invoke;

import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.calls.OutBinding;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
public class OutPrm implements OutBinding {
	protected @NonNull String fromOutParameter;
	protected @NonNull String toField;
	protected Doc doc;

	@Override
	public String fromOutParameter() {
		return fromOutParameter;
	}

	@Override
	public String toField() {
		return toField;
	}

	@Override
	public Optional<? extends Doc> getDoc() {
		return doc == null ? Optional.empty() : Optional.of(doc);
	}
}
