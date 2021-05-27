package org.werk2.core.config.prog.calls;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.calls.BatchCall;
import org.werk2.config.calls.Call;
import org.werk2.config.calls.Concurrency;
import org.werk2.core.config.prog.ProgDocumented;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ProgBatchCall extends ProgDocumented implements BatchCall {
    public ProgBatchCall(Optional<? extends Doc> doc, Optional<? extends List<? extends Call>> calls,
			Optional<? extends List<? extends BatchCall>> batches, Optional<Concurrency> concurrency) {
		super(doc);
		this.calls = calls;
		this.batches = batches;
		this.concurrency = concurrency;
	}
    
    @NonNull protected Optional<? extends List<? extends Call>> calls;
    @NonNull protected Optional<? extends List<? extends BatchCall>> batches;
    @NonNull protected Optional<Concurrency> concurrency;
}
