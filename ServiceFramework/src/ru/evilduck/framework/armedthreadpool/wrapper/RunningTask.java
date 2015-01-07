package ru.evilduck.framework.armedthreadpool.wrapper;

import java.io.Serializable;
import java.util.concurrent.Callable;

import android.os.ResultReceiver;

public  class RunningTask extends ComparableFutureTask<Serializable> {

	private ResultReceiver callback;
	private int id;
	
	public RunningTask(int id,Callable<Serializable> callable,ResultReceiver callback,int priority) {
		super(callable, priority);
		this.callback = callback;
		this.id=id;
	}


	public ResultReceiver getCallback() {
		return callback;
	}


	public int getId() {
		return id;
	}

}
