package ru.evilduck.framework.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PriorityExecutor extends ThreadPoolExecutor {
	 
	
	public static final int LOW_PRIORITY=0;
	public static final int NORMAL_PRIORITY=2;
	public static final int HIGH_PRIORITY=4;
	public static final int EXTRA_HIGH_PRIORITY=6;
	
    public PriorityExecutor(int corePoolSize, int maximumPoolSize,long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    } 
    
    //Utitlity method to create thread pool easily 

    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new PriorityExecutor(nThreads, nThreads, 0L,TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>());
    } 
    //Submit with New comparable task 

    public Future<?> submit(Runnable task, int priority) {
        return super.submit(new ComparableFutureTask(task, null, priority));
    } 
    public <T> Future<?> submit(Callable<T> callable, int priority) {
    	ComparableFutureTask<T> task=new ComparableFutureTask<T>(callable, priority); 
        return super.submit(task);
    } 
    //execute with New comparable task 

    public void execute(Runnable command, int priority) {
        super.execute(new ComparableFutureTask(command, null, priority));
    } 

    @Override 
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return (RunnableFuture<T>) callable;
    } 

    @Override 
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return (RunnableFuture<T>) runnable;
    } 
} 
