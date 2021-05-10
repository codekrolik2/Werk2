package org.werk2.common;

import java.util.Optional;

public interface TransitionStatus {
	TransitionType getTransitionType();
	Optional<StepRef> toStep();
	long getDelayMs();
}
