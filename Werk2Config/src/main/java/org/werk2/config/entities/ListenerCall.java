package org.werk2.config.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.calls.BatchCall;

/**
 * Listener's Event bindings and function name filter 
 * @author jamirov
 *
 */
public interface ListenerCall extends BatchCall {
	/**
	 * @return Event bindings
	 */
    public List<? extends Event> getEvents();
    /**
     * @return Function name filter
     */
    public Optional<String> getFunctionNameRegex();
}
