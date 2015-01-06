/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.evilduck.framework.armedthreadpool;

import java.util.concurrent.Callable;

/**
 *
 * @author sergey
 */
public class RunningFutureTask<T> extends ComparableFutureTask<T>{
   
        private String intent;
        public RunningFutureTask(Runnable runnable, T result, int priority) {
            super(runnable, result,priority);
        }

        public RunningFutureTask(Callable<T> callable, int priority,String intent) {
            super(callable,priority);
            this.intent=intent;
        }
        public String getIntent(){
            return intent;
        }
}
