package org.werk2.config.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Documented;

/**
 * Transitioner
 * 
 * Field OverrideChildListeners for (Transit->Transit) declarations doesn't make sense, 
 * because in order to override such listeners it's sufficient to declare a direct reference to underlying Function.
 * 
 * @author jamirov
 *
 */
public interface Transit extends Documented {
	/**
	 * @return Transitioner name (alias - will call underlying function + listeners)
	 */
	public String getTransitFunctionName();
	/**
	 * @return Underlying physical/logical function name
	 */
	public String getCalledFunctionName();
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
