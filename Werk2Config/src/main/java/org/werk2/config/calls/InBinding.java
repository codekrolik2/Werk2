package org.werk2.config.calls;

import java.util.Optional;

import org.werk2.config.Documented;

/**
 * Binding of field or constant value to input parameter of a function
 * @author jamirov
 */
public interface InBinding extends Documented {
	/**
	 * @return Field name
	 */
	public Optional<String> getFromField();
	/**
	 * @return Input parameter name
	 */
	public String getToInParameter();
	/**
	 * @return Constant value
	 */
	public Optional<String> getValue();
}
