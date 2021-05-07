package org.werk2.config.engine;

import org.werk2.config.Documented;

/**
 * Werk2 Engine parameters 
 * @author jamirov
 *
 */
public interface EngineParameter extends Documented {
	/**
	 * @return Parameter name
	 */
	public String getName();
	/**
	 * @return Parameter value
	 */
	public String getValue();
}
