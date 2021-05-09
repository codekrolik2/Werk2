package org.werk2.config.annotation.functions;

import java.util.Optional;

import org.werk2.config.Doc;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class AnnoDocEntry implements Doc {
    @NonNull protected Optional<String> title;
    @NonNull protected Optional<String> description;
	
	@Override
	public Optional<String> getTitle() {
		return title;
	}
	
	@Override
	public Optional<String> getDescription() {
		return description;
	}
}
