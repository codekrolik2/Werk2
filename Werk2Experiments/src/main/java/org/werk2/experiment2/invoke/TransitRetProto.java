package org.werk2.experiment2.invoke;

import org.werk2.common.TransitRet;
import org.werk2.common.TransitionStatus;

import lombok.NonNull;
import lombok.ToString;

@ToString(callSuper=true)
public class TransitRetProto extends RetProto implements TransitRet {
	protected @NonNull TransitionStatus transitionStatus;
	
	public TransitRetProto(int status, String statusMessage, TransitionStatus transitionStatus) {
		super(status, statusMessage);
		this.transitionStatus = transitionStatus;
	}

	public TransitRetProto(int status, TransitionStatus transitionStatus) {
		super(status);
		this.transitionStatus = transitionStatus;
	}

	@Override
	public TransitionStatus getTransitionStatus() {
		return transitionStatus;
	}
}

