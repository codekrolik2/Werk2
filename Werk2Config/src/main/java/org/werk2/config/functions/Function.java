package org.werk2.config.functions;

import java.util.List;

import org.werk2.config.Documented;

/**
 * Function definition
 * @author jamirov
 *
 */
public interface Function extends Documented {
	/**
	 * @return Function name
	 */
	public String getFunctionName();
	/**
	 * @return Function signatures
	 */
	public List<FunctionSignature> getSignatures();
}
