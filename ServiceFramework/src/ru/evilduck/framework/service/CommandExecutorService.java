/*
 * Copyright (C) 2013 Alexander Osmanov (http://perfectear.educkapps.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ru.evilduck.framework.service;

import java.util.concurrent.ConcurrentHashMap;

import ru.evilduck.framework.SFApplication;
import ru.evilduck.framework.armedthreadpool.ArmedThreadPool;
import ru.evilduck.framework.armedthreadpool.wrapper.CallableCommandWrapper;
import ru.evilduck.framework.armedthreadpool.wrapper.ComparableFutureTask;
import ru.evilduck.framework.armedthreadpool.wrapper.RunningTask;
import ru.evilduck.framework.handlers.BaseCommand;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

public class CommandExecutorService extends Service implements OnCompletedCommandListener,CommandExecutable {

	private static final int NUM_THREADS_OF_PARALLEL_EXECUTOR = 1;

	
	
	public static final String EXTRA_COMMAND_PRIORITY = SFApplication.PACKAGE.concat(".EXTRA_COMMAND_PRIORITY");

	private ArmedThreadPool executorParallel = ArmedThreadPool.newFixedThreadPool(NUM_THREADS_OF_PARALLEL_EXECUTOR);


	private ConcurrentHashMap<Integer, RunningTask> runningTasks = new ConcurrentHashMap<Integer, RunningTask>();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		executorParallel.setOnCompletedCommandListener(this);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		executorParallel.shutdownNow();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Test", "onStartCommand");
		if (ACTION_EXECUTE_COMMAND.equals(intent.getAction())) {
			RunningTask task=unpuckIntentToTask(intent);
			Log.d("Test", "Submit task and put it to wrapper queue");
			runningTasks.put(task.getId(), task);
			executorParallel.submit(task);
		}
		if (ACTION_CANCEL_COMMAND.equals(intent.getAction())) {
			RunningTask runningCommand = runningTasks.get(getCommandId(intent));
			if (runningCommand != null) {
				runningCommand.cancel(true);
			}
		}

		return START_NOT_STICKY;
	}

	private RunningTask unpuckIntentToTask(Intent intent){
		int priority=getPriority(intent);
		BaseCommand command=getCommand(intent);
		CallableCommandWrapper commandWrapper=new CallableCommandWrapper(getApplicationContext(), command);
		ResultReceiver resultReceiver=getReceiver(intent);
		int id =getCommandId(intent);
		return new RunningTask(id,commandWrapper,resultReceiver,priority);
	}
	
	

	private ResultReceiver getReceiver(Intent intent) {
		return intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
	}

	private BaseCommand getCommand(Intent intent) {
		return (BaseCommand) intent.getSerializableExtra(EXTRA_COMMAND);
	}
	private int getPriority(Intent intent) {
		return intent.getIntExtra(EXTRA_COMMAND_PRIORITY, ComparableFutureTask.NORMAL_PRIORITY);
	}

	private int getCommandId(Intent intent) {
		return intent.getIntExtra(EXTRA_REQUEST_ID, -1);
	}

	@Override
	public void onCompletedCommand(int id) {
		Log.d("Test", "onCompletedCommand");
		runningTasks.remove(id);
		if (runningTasks.isEmpty()) {
			Log.d("Test", "STOP SELF because No Tasks");
			stopSelf();
		}
		
	}
    
	
}
