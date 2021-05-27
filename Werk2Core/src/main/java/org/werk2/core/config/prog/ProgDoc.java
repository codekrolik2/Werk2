package org.werk2.core.config.prog;

import java.util.Optional;

import org.werk2.config.Doc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class ProgDoc implements Doc {
    @NonNull protected Optional<String> title;
    @NonNull protected Optional<String> description;
}
