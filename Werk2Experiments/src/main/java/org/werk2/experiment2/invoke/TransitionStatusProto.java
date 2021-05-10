package org.werk2.experiment2.invoke;

import java.util.Optional;

import org.werk2.common.StepRef;
import org.werk2.common.TransitionStatus;
import org.werk2.common.TransitionType;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class TransitionStatusProto implements TransitionStatus {
	@NonNull protected TransitionType type;
	protected long delayMs = 0;
	protected StepRef stepRef = null;
	
	@Override
	public TransitionType getTransitionType() {
		return type;
	}

	@Override
	public Optional<StepRef> toStep() {
		return stepRef == null ? Optional.empty() : Optional.of(stepRef);
	}

	@Override
	public long getDelayMs() {
		return delayMs;
	}
}
