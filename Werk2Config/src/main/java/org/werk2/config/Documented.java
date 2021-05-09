package org.werk2.config;

import java.util.Optional;

/**
 * Config element with documentation attached
 * @author jamirov
 *
 */
public interface Documented {
	/**
	 * @return Documentation
	 */
	Optional<? extends Doc> getDoc();
}
