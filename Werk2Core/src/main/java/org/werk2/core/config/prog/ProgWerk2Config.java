package org.werk2.core.config.prog;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.Werk2Config;
import org.werk2.config.engine.Engine;
import org.werk2.config.entities.Exec;
import org.werk2.config.entities.ExtendedFlow;
import org.werk2.config.entities.ExtendedStep;
import org.werk2.config.entities.Flow;
import org.werk2.config.entities.Step;
import org.werk2.config.entities.Transit;
import org.werk2.config.functions.Function;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ProgWerk2Config extends ProgDocumented implements Werk2Config {
    public ProgWerk2Config(Optional<? extends Doc> doc, Optional<? extends Engine> engine,
			Optional<? extends List<? extends Flow>> flows, Optional<? extends List<? extends Step>> steps,
			Optional<? extends List<? extends ExtendedFlow>> extendedFlows,
			Optional<? extends List<? extends ExtendedStep>> extendedSteps,
			Optional<? extends List<? extends Exec>> execs, Optional<? extends List<? extends Transit>> transits,
			Optional<? extends List<? extends Function>> rawFunctions) {
		super(doc);
		this.engine = engine;
		this.flows = flows;
		this.steps = steps;
		this.extendedFlows = extendedFlows;
		this.extendedSteps = extendedSteps;
		this.execs = execs;
		this.transits = transits;
		this.rawFunctions = rawFunctions;
	}
    
    @NonNull protected Optional<? extends Engine> engine;
    @NonNull protected Optional<? extends List<? extends Flow>> flows;
    @NonNull protected Optional<? extends List<? extends Step>> steps;
    @NonNull protected Optional<? extends List<? extends ExtendedFlow>> extendedFlows;
    @NonNull protected Optional<? extends List<? extends ExtendedStep>> extendedSteps;
    @NonNull protected Optional<? extends List<? extends Exec>> execs;
    @NonNull protected Optional<? extends List<? extends Transit>> transits;
    @NonNull protected Optional<? extends List<? extends Function>> rawFunctions;
}
