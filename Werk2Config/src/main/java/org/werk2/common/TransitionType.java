package org.werk2.common;

public enum TransitionType {
	NEXT_STEP,
	ROLLBACK,
	
	CALLBACK_WAIT,

	FINISH,
	FINISH_ROLLBACK,
	
	FAIL
}
