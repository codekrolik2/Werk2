package org.werk2.config.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Documented;
import org.werk2.config.calls.BatchCall;
import org.werk2.config.calls.Call;
import org.werk2.config.functions.Function;

/**
 * Step of a Flow
 * @author jamirov
 *
 */
public interface Step extends Documented {
	/**
	 * @return Step function definition
	 */
    public Function getFunction();
    /**
     * @return Execution blocks
     */
    public Optional<BatchCall> getExecBlock();
    /**
     * @return Transitioner
     */
    public Call getTransit();
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
