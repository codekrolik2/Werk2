package org.werk2.experiment2.invoke;

import org.werk2.common.Ret;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class RetProto implements Ret {
	protected int status;
	protected String statusMessage = null;
	
	public RetProto(int status) {
		this.status = status;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public String getStatusMessage() {
		return statusMessage;
	}
}
