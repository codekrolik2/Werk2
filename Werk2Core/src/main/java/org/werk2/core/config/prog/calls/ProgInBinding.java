package org.werk2.core.config.prog.calls;

import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.calls.InBinding;
import org.werk2.core.config.prog.ProgDocumented;

import lombok.NonNull;

public class ProgInBinding extends ProgDocumented implements InBinding {
	public ProgInBinding(Optional<? extends Doc> doc, String fromField_, String toInParameter_,
			Optional<String> value) {
		super(doc);
		this.fromField_ = fromField_;
		this.toInParameter_ = toInParameter_;
		this.value = value;
	}

	@NonNull protected String fromField_;
	@NonNull protected String toInParameter_;
	@NonNull protected Optional<String> value;

	public String fromField() {
		return fromField_;
	}
	
	public String toInParameter() {
		return toInParameter_;
	}
	
	public Optional<String> getValue() {
		return value;
	}
}
