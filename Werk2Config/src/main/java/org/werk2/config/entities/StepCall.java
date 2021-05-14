package org.werk2.config.entities;

import java.util.Optional;

import org.werk2.config.calls.BatchCall;

public interface StepCall extends BatchCall {
	/**
	 * If the same Step is used in the Flow several times with different parameters,
	 * those are two different calls and should be denoted as different logical Steps.
	 *  
	 * @return Step alias  
	 */
    public Optional<String> getStepAlias();
}
