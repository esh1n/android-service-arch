/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.evilduck.framework.armedthreadpool;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.evilduck.framework.armedthreadpool.wrapper.CallableCommandWrapper;
import ru.evilduck.framework.armedthreadpool.wrapper.ComparableFutureTask;
import ru.evilduck.framework.armedthreadpool.wrapper.RunningTask;
import ru.evilduck.framework.armedthreadpool.wrapper.RunningTaskWithPriority;
import ru.evilduck.framework.handlers.BaseCommand;
import ru.evilduck.framework.manager.ErrorProcessor;
import ru.evilduck.framework.manager.NotifySubscriberUtil;
import ru.evilduck.framework.service.interfaces.OnCompletedCommandListener;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * 
 * @author sergey
 */
public class ArmedThreadPool extends ThreadPoolExecutor {

	private static final String TAG = "Test";
    private OnCompletedCommandListener onCompletedCommandListener;
	public ArmedThreadPool(int corePoolSize, int maximumPoolSize,long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	// Utitlity method to create thread pool easily

	public static ArmedThreadPool newFixedThreadPool(int nThreads) {
		return new ArmedThreadPool(nThreads, nThreads, 0L,TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>());
	}

	// Submit with New comparable task

	public Future<?> submit(Runnable task, int priority) {
		return super.submit(new ComparableFutureTask(task, null, priority));
	}

	// execute with New comparable task

	public void execute(Runnable command, int priority) {
		super.execute(new ComparableFutureTask(command, null, priority));
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		return (RunnableFuture<T>) callable;
	}

	/**
	 * 
	 * @param <T>
	 * @param command
	 * @param priority
	 * @param intent
	 * @return
	 */
	public <T extends Serializable> Future<?> submit(BaseCommand<T> command,int priority, ResultReceiver callback,int id, Context context) {
		CallableCommandWrapper wrapper = new CallableCommandWrapper(context, command);
		RunningTaskWithPriority task = new RunningTaskWithPriority(id,wrapper,callback, priority);
		return super.submit(task);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		return (RunnableFuture<T>) runnable;
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		if (r != null && r instanceof RunningTask) {
			RunningTask futureTask = (RunningTask) r;
			ResultReceiver callback = futureTask.getCallback();
			if (t == null) {
				try {
					if (futureTask.isDone()) {
						Serializable result = (Serializable) futureTask.get();
						if (result != null) {
							NotifySubscriberUtil.notifySuccess(result, callback);
						}
						else{
							NotifySubscriberUtil.notifyNullResult(callback);
						}
					}
				} catch (CancellationException ce) {
					t = ce;
				} catch (ExecutionException ee) {
					t = ee.getCause();
				} catch (InterruptedException ie) {
					t = ie;
					Thread.currentThread().interrupt(); // ignore/reset
				}
			} else {
				Log.e(TAG, "exception ib throwable afterexecute");
				System.out.println(" ");
			}
			if (t != null) {
				Log.e(TAG, "Error in ThreadPool " + t);
				Bundle exceptionBunble=ErrorProcessor.getExceptionBundle(t);
				NotifySubscriberUtil.notifyFailure(exceptionBunble, callback);
			}
			onCompleteCommand(futureTask.getId());
		}
		
	}
	private void onCompleteCommand(int id){
		if(onCompletedCommandListener!=null){
			onCompletedCommandListener.onCompletedCommand(id);
		}
	}

	public OnCompletedCommandListener getOnCompletedCommandListener() {
		return onCompletedCommandListener;
	}

	public void setOnCompletedCommandListener(OnCompletedCommandListener onCompletedCommandListener) {
		this.onCompletedCommandListener = onCompletedCommandListener;
	}

}
