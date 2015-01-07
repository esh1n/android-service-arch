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
import java.util.concurrent.ExecutorService;

import ru.evilduck.framework.SFApplication;
import ru.evilduck.framework.armedthreadpool.ArmedThreadPool;
import ru.evilduck.framework.armedthreadpool.wrapper.CallableCommandWrapper;
import ru.evilduck.framework.armedthreadpool.wrapper.ComparableFutureTask;
import ru.evilduck.framework.armedthreadpool.wrapper.RunningPriorityTask;
import ru.evilduck.framework.armedthreadpool.wrapper.RunningTask;
import ru.evilduck.framework.handlers.BaseCommand;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

public class CommandExecutorService extends Service implements OnCompletedCommandListener {

	private static final int NUM_THREADS_OF_PARALLEL_EXECUTOR = 1;

	public static final String ACTION_EXECUTE_COMMAND = SFApplication.PACKAGE.concat(".ACTION_EXECUTE_COMMAND");

	public static final String ACTION_CANCEL_COMMAND = SFApplication.PACKAGE.concat(".ACTION_CANCEL_COMMAND");

	public static final String EXTRA_REQUEST_ID = SFApplication.PACKAGE.concat(".EXTRA_REQUEST_ID");

	public static final String EXTRA_STATUS_RECEIVER = SFApplication.PACKAGE.concat(".STATUS_RECEIVER");

	public static final String EXTRA_COMMAND = SFApplication.PACKAGE.concat(".EXTRA_COMMAND");
	
	public static final String EXTRA_COMMAND_PRIORITY = SFApplication.PACKAGE.concat(".EXTRA_COMMAND_PRIORITY");

	public static final String EXTRA_TRANCSACTIONAL_EXECUTION_MODE = SFApplication.PACKAGE.concat(".EXTRA_EXECUTION_MODE");;

	private ArmedThreadPool executorParallel = ArmedThreadPool.newFixedThreadPoolWithPriority(NUM_THREADS_OF_PARALLEL_EXECUTOR);
	private ArmedThreadPool executorTransactional = ArmedThreadPool.newSingleThreadExecutor();

	private ConcurrentHashMap<Integer, RunningPriorityTask> parallerRunningTasks = new ConcurrentHashMap<Integer, RunningPriorityTask>();
	private ConcurrentHashMap<Integer, RunningTask> trancsactionalRunningTasks = new ConcurrentHashMap<Integer, RunningTask>();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		executorParallel.setOnCompletedCommandListener(this);
		executorTransactional.setOnCompletedCommandListener(this);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		executorTransactional.shutdownNow();
		executorParallel.shutdownNow();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Test", "onStartCommand");
		if (ACTION_EXECUTE_COMMAND.equals(intent.getAction())) {
			boolean isTransactionalMode=intent.getBooleanExtra(EXTRA_TRANCSACTIONAL_EXECUTION_MODE, true);
			if(isTransactionalMode){
				Log.d("Test", "TransactionalMode ");
				RunningTask task=unpuckIntentToSimpleTask(intent);
				trancsactionalRunningTasks.put(task.getId(), task);
				executorTransactional.submit(task);
			}
			else{
				Log.d("Test", "ParallelMode");
				RunningPriorityTask task=unpuckIntentToTask(intent);
				Log.d("Test", "Submit task and put it to wrapper queue");
				parallerRunningTasks.put(task.getId(), task);
				executorParallel.submit(task);
			}
			
		}
		if (ACTION_CANCEL_COMMAND.equals(intent.getAction())) {
			int commandId=getCommandId(intent);
			cancelCommand(commandId);
		}

		return START_NOT_STICKY;
	}

	private void cancelCommand(int commandId) {
		RunningTask  runningCommand= parallerRunningTasks.get(commandId);
		//TODO merge two collections
		if (runningCommand == null) {
			runningCommand=trancsactionalRunningTasks.get(commandId);
		}
		if(runningCommand!=null){
			runningCommand.cancel(true);
		}
	}

	private RunningPriorityTask unpuckIntentToTask(Intent intent){
		int priority=getPriority(intent);
		BaseCommand command=getCommand(intent);
		CallableCommandWrapper commandWrapper=new CallableCommandWrapper(getApplicationContext(), command);
		ResultReceiver resultReceiver=getReceiver(intent);
		int id =getCommandId(intent);
		return new RunningPriorityTask(id,commandWrapper,resultReceiver,priority);
	}
	private RunningTask unpuckIntentToSimpleTask(Intent intent){
		BaseCommand command=getCommand(intent);
		CallableCommandWrapper commandWrapper=new CallableCommandWrapper(getApplicationContext(), command);
		ResultReceiver resultReceiver=getReceiver(intent);
		int id =getCommandId(intent);
		return new RunningTask(id,commandWrapper,resultReceiver);
	}
	
	

	private ResultReceiver getReceiver(Intent intent) {
		return intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
	}

	private BaseCommand getCommand(Intent intent) {
		return (BaseCommand) intent.getSerializableExtra(EXTRA_COMMAND);
	}
	private int getPriority(Intent intent) {
		return intent.getIntExtra(EXTRA_COMMAND_PRIORITY, RunningPriorityTask.NORMAL_PRIORITY);
	}

	private int getCommandId(Intent intent) {
		return intent.getIntExtra(EXTRA_REQUEST_ID, -1);
	}

	@Override
	public void onCompletedCommand(int id) {
		Log.d("Test", "onCompletedCommand");
		//TODO FIX PERFOMANCE
		if(parallerRunningTasks.containsKey(id)){
			parallerRunningTasks.remove(id);
		}else if(trancsactionalRunningTasks.containsKey(id)){
			trancsactionalRunningTasks.remove(id);	
		}
		if (parallerRunningTasks.isEmpty()&&trancsactionalRunningTasks.isEmpty()) {
			Log.d("Test", "STOP SELF because No Tasks");
			stopSelf();
		}	
		
		
	}
    
	
}
