package org.werk2.core.config;

import org.werk2.common.Ret;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class RetImpl implements Ret {
	protected int status;
	protected String statusMessage = null;
	
	public RetImpl(int status) {
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
