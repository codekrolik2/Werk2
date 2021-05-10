package org.werk2.config.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Documented;

/**
 * Executioner function
 * @author jamirov
 *
 */
public interface Exec extends Documented {
	/**
	 * @return Executioner function name (alias)
	 */
	public String getExecFunctionName();
	/**
	 * @return Bound listeners
	 */
    public Optional<? extends List<? extends ListenerCall>> getListeners();
    /**
     * default false
     * @return true - override (do not use) listeners defined on parent levels
     */
    public Optional<Boolean> getOverrideListeners();
}
