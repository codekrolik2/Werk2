package org.werk2.config.engine;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Documented;
import org.werk2.config.entities.ListenerCall;

/**
 * Werk2 Engine
 * @author jamirov
 */
public interface Engine extends Documented {
	/**
	 * @return Engine parameters
	 */
    public Optional<List<EngineParameter>> getParameters();
    /**
     * @return Engine-level listener bindings
     */
    public Optional<List<ListenerCall>> getListeners();
}
