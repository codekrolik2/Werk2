package org.werk2.config;

import java.util.List;
import java.util.Optional;

import org.werk2.config.engine.Engine;
import org.werk2.config.entities.Exec;
import org.werk2.config.entities.Flow;
import org.werk2.config.entities.Step;
import org.werk2.config.entities.Transit;
import org.werk2.config.functions.Function;

/**
 * Werk2 Configuration
 * @author jamirov
 */
public interface Werk2Config extends Documented {
	/**
	 * @return Engine
	 */
    public Optional<Engine> getEngine();
    /**
     * @return Flows
     */
    public Optional<List<Flow>> getFlows();
    /**
     * @return Steps
     */
    public Optional<List<Step>> getSteps();
    /**
     * @return Executioner functions
     */
    public Optional<List<Exec>> getExecs();
    /**
     * @return Transitioner functions
     */
    public Optional<List<Transit>> getTransits();
    /**
     * @return Raw function (code)
     */
    public Optional<List<Function>> getRawFunctions();
}
