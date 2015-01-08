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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ru.evilduck.framework.SFApplication;
import ru.evilduck.framework.armedthreadpool.ArmedThreadPool;
import ru.evilduck.framework.armedthreadpool.wrapper.CallableCommandWrapper;
import ru.evilduck.framework.armedthreadpool.wrapper.ComparableFutureTask;
import ru.evilduck.framework.armedthreadpool.wrapper.RunningTask;
import ru.evilduck.framework.armedthreadpool.wrapper.RunningTaskWithPriority;
import ru.evilduck.framework.handlers.BaseCommand;
import ru.evilduck.framework.service.interfaces.CommandExecutable;
import ru.evilduck.framework.service.interfaces.OnCompletedCommandListener;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

public class CommandExecutorService extends Service implements
		OnCompletedCommandListener, CommandExecutable {

	private static final int NUM_THREADS_OF_PARALLEL_EXECUTOR = 1;

	private ArmedThreadPool executorParallel = ArmedThreadPool.newFixedThreadPool(NUM_THREADS_OF_PARALLEL_EXECUTOR);

	private ConcurrentHashMap<Integer, RunningTask> runningTasks = new ConcurrentHashMap<Integer, RunningTask>();
	private Map<String, ExecutorAction> serviceActions = new HashMap<String, ExecutorAction>();

	private TaskIntentPucker taskIntentPucker = new TaskIntentPucker();;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		executorParallel.setOnCompletedCommandListener(this);
		assignActionToService();
	}

	private void assignActionToService() {
		serviceActions.put(ACTION_EXECUTE_COMMAND, executeCommandAction);
		serviceActions.put(ACTION_CANCEL_COMMAND, cancelCommandAction);
	}

	private ExecutorAction executeCommandAction = new ExecutorAction() {

		@Override
		public void execute() {
			RunningTask task = unpuckIntentToTask();
			Log.d("Test", "Submit task and put it to wrapper queue");
			runningTasks.put(task.getId(), task);
			executorParallel.submit(task);
		}
	};
	
	private ExecutorAction cancelCommandAction  = new ExecutorAction() {

		@Override
		public void execute() {
			int commandId = taskIntentPucker.getCommandId();
			RunningTask runningCommand = runningTasks.get(commandId);
			if (runningCommand != null) {
				runningCommand.cancel(true);
			}
		}
	};
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		executorParallel.shutdownNow();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Test", "onStartCommand");
		taskIntentPucker.replaceIntent(intent);
		ExecutorAction action=serviceActions.get(intent.getAction());
		if(action!=null){
			action.execute();
		}
		return START_NOT_STICKY;
	}

	@Override
	public RunningTask unpuckIntentToTask() {
		int priority = taskIntentPucker.getPriority();
		BaseCommand command = taskIntentPucker.getCommand();
		ResultReceiver resultReceiver = taskIntentPucker.getReceiver();
		int id = taskIntentPucker.getCommandId();
		CallableCommandWrapper commandWrapper = new CallableCommandWrapper(getApplicationContext(), command);
		return new RunningTaskWithPriority(id, commandWrapper, resultReceiver,priority);
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
