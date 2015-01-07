/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.evilduck.framework.armedthreadpool.wrapper;

import java.io.Serializable;
import java.util.concurrent.Callable;

import ru.evilduck.framework.handlers.BaseCommand;
import android.content.Context;
import android.util.Log;

/**
 *
 * @author sergey
 */
public class CallableCommandWrapper<T extends Serializable> implements Callable<T> {

    private Context context;
    private BaseCommand<T> executable;

    public CallableCommandWrapper(Context context, BaseCommand<T> executable) {
        this.context = context;
        this.executable = executable;
    }

    @Override
    public T call() throws Exception {
    	Log.d("Test", "execute command");
    	Thread.sleep(500);
        return executable.execute(context);
    }

}
