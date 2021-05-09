package org.werk2.config.functions;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Documented;

/**
 * Definition of a single call signature of a function
 * @author jamirov
 *
 */
public interface FunctionSignature extends Documented {
	/**
	 * @return function parameters
	 */
    public Optional<? extends List<? extends FunctionParameter>> getParameters();
}
