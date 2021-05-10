package org.werk2.config.functions;

import java.util.Optional;

import org.werk2.config.Documented;

/**
 * Function parameter definition
 * @author jamirov
 *
 */
public interface FunctionParameter extends Documented {
	/**
	 * @return Parameter name
	 */
	public String getName();
	/**
	 * @return IN or OUT parameter
	 */
	public ParameterDirection getDirection();
	/**
	 * @return Parameter type
	 */
	public ParameterType getType();
	/**
	 * Logical functions may have runtime parameter type set for their parameters.
	 * Physical functions must have runtime parameter type set for their parameters.
	 * All RUNTIME parameters for logical and physical functions must have runtime parameter type set.
	 * 
	 * @return Raw Type for the parameter
	 */
	public Optional<String> getRuntimeType();
    /**
     * default SYSTEM_DEFAULT
     * @return parameter passing mechanism
     */
	public Optional<ParameterPassing> getPassing();
}
