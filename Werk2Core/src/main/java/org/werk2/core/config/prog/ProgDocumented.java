package org.werk2.core.config.prog;

import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.Documented;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class ProgDocumented implements Documented {
	@NonNull protected Optional<? extends Doc> doc;
}
