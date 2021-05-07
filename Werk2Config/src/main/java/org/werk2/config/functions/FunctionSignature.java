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
	 * @return Input parameters
	 */
    public Optional<List<FunctionParameter>> getInParameters();
    /**
     * @return Output parameters
     */
    public Optional<List<FunctionParameter>> getOutParameters();
}
