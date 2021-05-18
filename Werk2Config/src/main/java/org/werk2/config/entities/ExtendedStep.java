package org.werk2.config.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Documented;
import org.werk2.config.calls.BatchCall;
import org.werk2.config.calls.Call;
import org.werk2.config.functions.FunctionSignature;

/**
 * A Step that extends an existing Step (inheritance)
 * @author jamirov
 *
 */
public interface ExtendedStep extends Documented {
	/**
	 * @return Name of a SuperStep
	 */
	public String getSuperStepFunctionName();
	/**
	 * @return Name of this Step
	 */
	public String getNewFunctionName();
	/**
	 * @return Additional signatures
	 */
	public Optional<? extends List<? extends FunctionSignature>> getAddSignatures();
    /**
     * default false
     * @return true - override (do not use) signatures defined on SuperStep levels
     */
	public Optional<Boolean> getDropOldSignatures();
	/**
	 * @return Additional execution blocks
	 */
    public Optional<BatchCall> getAddExecBlock();
    /**
     * default false
     * @return true - override (do not use) execution blocks defined on SuperStep levels
     */
	public Optional<Boolean> getDropOldExecBlock();
	/**
	 * @return New Transitioner
	 */
    public Optional<? extends Call> getNewTransit();
    /**
     * @return Additional Listeners
     */
    public Optional<? extends List<? extends ListenerCall>> getAddListeners();
    /**
     * default false
     * @return true - override (do not use) listeners defined on SuperStep levels
     */
    public Optional<Boolean> getDropOldListeners();
    /**
     * @return New "OverrideListeners" behavior
     */
    public Optional<Boolean> getNewOverrideListeners();
}
