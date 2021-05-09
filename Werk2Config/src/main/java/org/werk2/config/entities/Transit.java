package org.werk2.config.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Documented;

/**
 * Transitioner
 * @author jamirov
 *
 */
public interface Transit extends Documented {
	/**
	 * @return Transitioner function name (alias)
	 */
	public String getTransitFunctionName();
	/**
	 * @return Raw function name
	 */
	public String getRawFunctionName();
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
