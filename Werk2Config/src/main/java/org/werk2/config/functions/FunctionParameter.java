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
	 * @return Parameter type
	 */
	public ParameterType getType();
	/**
	 * @return If Type - RUNTIME, specify Raw Type
	 */
	public Optional<String> getRuntimeType();
    /**
     * default BY_REF
     * @return parameter passing mechanism
     */
	public Optional<ParameterPassing> getPassing();
}
