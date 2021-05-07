package org.werk2.config;

import java.util.Optional;

/**
 * Documentation on config element 
 * @author jamirov
 *
 */
public interface Doc {
	/**
	 * @return Title
	 */
    public Optional<String> getTitle();
    /**
     * @return Element description
     */
    public Optional<String> getDescription();
}
