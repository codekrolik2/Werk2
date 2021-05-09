package org.werk2.config.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Documented;
import org.werk2.config.calls.Call;
import org.werk2.config.functions.Function;

/**
 * Flow of work
 * @author jamirov
 *
 */
public interface Flow extends Documented {
	/**
	 * @return Flow function definition
	 */
    public Function getFunction();
    /**
     * @return First step
     */
    public Call getFirstStep();
    /**
     * @return Steps
     */
    public Optional<? extends List<? extends Call>> getSteps();
    /**
     * @return Listener bindings
     */
    public Optional<? extends List<? extends ListenerCall>> getListeners();
    /**
     * default false
     * @return true - override (do not use) listeners defined on parent levels
     */
    public Optional<Boolean> getOverrideListeners();
}
