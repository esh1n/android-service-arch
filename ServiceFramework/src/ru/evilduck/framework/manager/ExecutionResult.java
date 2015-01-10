package ru.evilduck.framework.manager;

import java.io.Serializable;

public class ExecutionResult {

	private Serializable result;
	private Throwable throwable;
	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	public Serializable getResult() {
		return result;
	}
	public void setResult(Serializable result) {
		this.result = result;
	}
}
