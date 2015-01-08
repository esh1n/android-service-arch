package ru.evilduck.framework.armedthreadpool.wrapper;

import java.io.Serializable;
import java.util.concurrent.Callable;

import android.os.ResultReceiver;
import android.util.Log;

public class RunningTaskWithPriority<T extends Serializable> extends RunningTask<T> implements Comparable<RunningTaskWithPriority<T>> {

	public static final int LOW_PRIORITY = 4;
	public static final int NORMAL_PRIORITY = 3;
	public static final int HIGH_PRIORITY = 2;
	public static final int EXTRA_HIGH_PRIORITY = 1;

	volatile int priority;

	public RunningTaskWithPriority(int id, Callable<T> callable,ResultReceiver callback, int priority) {
		super(id, callable, callback);
		this.priority = priority;
		 Log.d("Test","this id "+id+" priority in Constructor "+priority);
	}

	@Override
	public int compareTo(RunningTaskWithPriority<T> o) {
		return  Integer.valueOf(priority).compareTo(o.getPriority());
	}

	public int getPriority() {
		return priority;
	}

}