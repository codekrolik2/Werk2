package org.werk2.config.functions;

import java.util.List;
import java.util.Optional;

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
	 * @return Function physical name (raw functions only)
	 */
	public Optional<String> getPhysicalName();
	/**
	 * @return Function signatures
	 */
	public List<? extends FunctionSignature> getSignatures();
}
