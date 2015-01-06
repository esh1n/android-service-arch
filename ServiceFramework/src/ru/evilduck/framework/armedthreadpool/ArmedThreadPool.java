/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.evilduck.framework.armedthreadpool;

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

/**
 *
 * @author sergey
 */
public class ArmedThreadPool extends ThreadPoolExecutor {

        public ArmedThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }
        //Utitlity method to create thread pool easily 

        public static ExecutorService newFixedThreadPool(int nThreads) {
            return new ArmedThreadPool(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>());
        }
        //Submit with New comparable task 

        public Future<?> submit(Runnable task, int priority) {
            return super.submit(new ComparableFutureTask(task, null, priority));
        }
        //execute with New comparable task 

        public void execute(Runnable command, int priority) {
            super.execute(new ComparableFutureTask(command, null, priority));
        }

        @Override
        protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
            return (RunnableFuture<T>) callable;
        }

        public <T> Future<?> submit(Callable<T> command, int priority,String intent) {
            RunningFutureTask<T> task = new RunningFutureTask<T>(command, priority,intent);
            return super.submit(task);
        }

        @Override
        protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
            return (RunnableFuture<T>) runnable;
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            if (t == null && r instanceof Future<?>) {
                try {
                    RunningFutureTask<?> futureTask = (RunningFutureTask<?>) r;
                    if (futureTask.isDone()) {
                        String result = (String) futureTask.get();
                        System.out.println("result " + result);
                        String intent =futureTask.getIntent();
                        System.out.println("intent " + intent);
                    }
                } catch (CancellationException ce) {
                    System.out.println("exception in CancellationException " );
                    t = ce;
                } catch (ExecutionException ee) {
                       System.out.println("exception in ExecutionException " );
                    t = ee.getCause();
                } catch (InterruptedException ie) {
                    System.out.println("exception in interrupted " );
                    Thread.currentThread().interrupt(); // ignore/reset
                }
            }else{
                  System.out.println("exception ib throwable afterexecute " );
            }
            if (t != null) {
                System.out.println(t);
            }
        }

    }
