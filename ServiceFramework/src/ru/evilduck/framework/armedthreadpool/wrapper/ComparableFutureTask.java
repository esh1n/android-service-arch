package ru.evilduck.framework.armedthreadpool.wrapper;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import android.util.Log;

public class ComparableFutureTask<T> extends FutureTask<T> implements Comparable<ComparableFutureTask<T>> {
	 
    volatile int priority = 0;

    public ComparableFutureTask(Runnable runnable, T result, int priority) {
        super(runnable, result);
        this.priority = priority;
    } 
 

    public ComparableFutureTask(Callable<T> callable, int priority) {
        super(callable);
        this.priority = priority;
        Log.d("Test","this priority in Constructor "+priority);
    } 

    @Override 
    public int compareTo(ComparableFutureTask<T> o) {
    	int compareResult=Integer.valueOf(priority).compareTo(o.getPriority());
    	Log.d("Test","this piority "+priority);
    	Log.d("Test","concurent piority "+o.priority);
        return compareResult;
    } 
    public int getPriority(){
    	return priority;
    }
} 
