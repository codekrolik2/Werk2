package org.werk2.config.calls;

import org.werk2.config.Documented;

/**
 * Binding of an output parameter of a function to a field
 * @author jamirov
 *
 */
public interface OutBinding extends Documented {
	/**
	 * @return Output parameter name
	 */
    public String getFromOutParameter();
    /**
     * @return Field name
     */
    public String getToField();
}
