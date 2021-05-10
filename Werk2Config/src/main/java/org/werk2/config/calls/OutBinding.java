package org.werk2.config.calls;

import org.werk2.config.Documented;

/**
 * Binding of an output parameter of a function to a field.
 * Out parameter must be of type org.werk2.common.OutParam<T>
 * @author jamirov
 *
 */
public interface OutBinding extends Documented {
	/**
	 * @return Output parameter name
	 */
    public String fromOutParameter();
    /**
     * @return Field name
     */
    public String toField();
}
