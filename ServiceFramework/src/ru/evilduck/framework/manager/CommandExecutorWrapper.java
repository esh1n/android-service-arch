package ru.evilduck.framework.manager;

import java.io.Serializable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import ru.evilduck.framework.armedthreadpool.wrapper.RunningTask;

public class CommandExecutorWrapper {

	public static ExecutionResult tryCommand(RunningTask task) {
		ExecutionResult result = new ExecutionResult();
		try {
			result.setResult((Serializable) task.get());
		} catch (CancellationException ce) {
			result.setThrowable(ce);
		} catch (ExecutionException ee) {
			result.setThrowable(ee.getCause());
		} catch (InterruptedException ie) {
			result.setThrowable(ie);
			Thread.currentThread().interrupt(); // ignore/reset
		}
		return result;
	}
}
