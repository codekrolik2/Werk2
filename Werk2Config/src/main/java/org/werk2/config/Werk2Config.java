package org.werk2.config;

import java.util.List;
import java.util.Optional;

import org.werk2.config.engine.Engine;
import org.werk2.config.entities.Exec;
import org.werk2.config.entities.ExtendedFlow;
import org.werk2.config.entities.ExtendedStep;
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
    public Optional<? extends Engine> getEngine();
    /**
     * @return Flows
     */
    public Optional<? extends List<? extends Flow>> getFlows();
    /**
     * @return Steps
     */
    public Optional<? extends List<? extends Step>> getSteps();
    /**
     * @return Extended Flows
     */
    public Optional<? extends List<? extends ExtendedFlow>> getExtendedFlows();
    /**
     * @return Extended Steps
     */
    public Optional<? extends List<? extends ExtendedStep>> getExtendedSteps();
    /**
     * @return Executioner functions
     */
    public Optional<? extends List<? extends Exec>> getExecs();
    /**
     * @return Transitioner functions
     */
    public Optional<? extends List<? extends Transit>> getTransits();
    /**
     * @return Raw exec function (code)
     */
    public Optional<? extends List<? extends Function>> getRawExecFunctions();
    /**
     * @return Raw transit function (code)
     */
    public Optional<? extends List<? extends Function>> getRawTransitFunctions();
}
