package org.werk2.config.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Documented;

/**
 * Executioner function
 * 
 * Field OverrideChildListeners for (Exec->Exec) and (Exec->Transit) declarations doesn't make sense, 
 * because in order to override such listeners it's sufficient to declare a direct reference to underlying Function.
 * 
 * @author jamirov
 *
 */
public interface Exec extends Documented {
	/**
	 * @return Executioner function name (alias)
	 */
	public String getExecFunctionName();
	/**
	 * @return Underlying physical/logical function name
	 */
	public String getCalledFunctionName();
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
