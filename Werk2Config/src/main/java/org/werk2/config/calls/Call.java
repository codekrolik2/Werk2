package org.werk2.config.calls;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Documented;

/**
 * Call describes a function call
 * @author jamirov
 */
public interface Call extends Documented {
	/**
	 * @return Name of the function to call 
	 */
    public String getFunctionName();
    /**
     * @return Input parameter bindings
     */
    public Optional<? extends List<? extends InBinding>> getInParameters();
    /**
     * @return Output parameter bindings
     */
    public Optional<? extends List<? extends OutBinding>> getOutParameters();
    /**
     * @return Status binding
     */
    public Optional<String> getOutStatusBinding();
    /**
     * @return Status message binding
     */
    public Optional<String> getOutStatusMessageBinding();
    /**
     * @return Transition status binding
     */
    public Optional<String> getOutTransitionStatusBinding();
    /**
     * default SYNCHRONIZED 
     * @return concurrency level
     */
    public Optional<Concurrency> getConcurrency();
}
