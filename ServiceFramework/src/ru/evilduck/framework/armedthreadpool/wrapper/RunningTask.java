package ru.evilduck.framework.armedthreadpool.wrapper;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import android.os.ResultReceiver;

public class RunningTask<T extends Serializable> extends FutureTask<T> {
	
	private ResultReceiver callback;
	private int id;

	public RunningTask(int id,Callable<T> callable,ResultReceiver callback ) {
		super(callable);
		this.id=id;
		this.callback=callback;
	}
	
	public ResultReceiver getCallback() {
		return callback;
	}


	public int getId() {
		return id;
	}
	
}
