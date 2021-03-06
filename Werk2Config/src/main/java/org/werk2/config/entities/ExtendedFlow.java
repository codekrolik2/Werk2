package org.werk2.config.entities;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Doc;
import org.werk2.config.Documented;
import org.werk2.config.functions.FunctionSignature;

/**
 * A Flow that extends an existing Flow (inheritance)
 * @author jamirov
 *
 */
public interface ExtendedFlow extends Documented {
	/**
	 * @return Function Documentation
	 */
	Optional<? extends Doc> getFunctionDoc();
	/**
	 * @return Name of a SuperFlow
	 */
	public String getSuperFlowFunctionName();
	/**
	 * @return Name of this Flow
	 */
	public String getNewFunctionName();

	/**
	 * @return Additional function signatures
	 */
	public Optional<? extends List<? extends FunctionSignature>> getAddSignatures();
    /**
     * default false
     * @return true - override (do not use) signatures defined on SuperFlow levels
     */
	public Optional<Boolean> getDropOldSignatures();

	/**
	 * @return New First step
	 */
    public Optional<? extends StepCall> getNewFirstStep();
    /**
     * @return Additional steps
     */
    public Optional<? extends List<? extends StepCall>> getAddSteps();
    /**
     * default false
     * @return true - override (do not use) steps defined on SuperFlow levels
     */
	public Optional<Boolean> getDropOldSteps();
    /**
     * @return Additional listeners
     */
    public Optional<? extends List<? extends ListenerCall>> getAddListeners();
    /**
     * default false
     * @return true - override (do not use) listeners defined on SuperFlow levels
     */
    public Optional<Boolean> getDropOldListeners();
    /**
     * @return New "OverrideListeners" behavior
     */
    public Optional<Boolean> getNewOverrideListeners();
}
