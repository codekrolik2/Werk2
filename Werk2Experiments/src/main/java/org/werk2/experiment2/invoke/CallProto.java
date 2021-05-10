package org.werk2.experiment2.invoke;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.calls.Call;
import org.werk2.config.calls.Concurrency;
import org.werk2.config.calls.InBinding;
import org.werk2.config.calls.OutBinding;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class CallProto implements Call {
	@NonNull String functionName;
	List<InBinding> inParameters;
	List<OutBinding> outParameters;
	String outStatusBinding;
	String outStatusMessageBinding;
	String outTransitionStatusBinding;
	Concurrency concurrency;
	
	@Override
	public String getFunctionName() {
		return functionName;
	}

	@Override
	public Optional<? extends List<? extends InBinding>> getInParameters() {
		return inParameters == null || inParameters.isEmpty() ? Optional.empty() : Optional.of(inParameters);
	}

	@Override
	public Optional<? extends List<? extends OutBinding>> getOutParameters() {
		return outParameters == null || outParameters.isEmpty() ? Optional.empty() : Optional.of(outParameters);
	}

	@Override
	public Optional<String> getOutStatusBinding() {
		return outStatusBinding == null ? Optional.empty() : Optional.of(outStatusBinding);
	}

	@Override
	public Optional<String> getOutStatusMessageBinding() {
		return outStatusMessageBinding == null ? Optional.empty() : Optional.of(outStatusMessageBinding);
	}

	@Override
	public Optional<String> getOutTransitionStatusBinding() {
		return outTransitionStatusBinding == null ? Optional.empty() : Optional.of(outTransitionStatusBinding);
	}

	@Override
	public Optional<Concurrency> getConcurrency() {
		return concurrency == null ? Optional.empty() : Optional.of(concurrency);
	}
	
	@Override public Optional<? extends Doc> getDoc() { return Optional.empty(); }
}
