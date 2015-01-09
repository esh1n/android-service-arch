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
package ru.evilduck.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import ru.evilduck.framework.armedthreadpool.wrapper.ComparableFutureTask;
import ru.evilduck.framework.handlers.BaseCommand;
import ru.evilduck.framework.handlers.implemetation.ConcatenateCommand;
import ru.evilduck.framework.manager.NotifySubscriberUtil;
import ru.evilduck.framework.manager.TaskIntentPucker;
import ru.evilduck.framework.service.CommandExecutorService;
import ru.evilduck.framework.service.interfaces.CommandExecutable;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.util.SparseArray;

public class SFServiceHelper {

	private ArrayList<SFServiceCallbackListener> currentListeners = new ArrayList<SFServiceCallbackListener>();

	private AtomicInteger idCounter = new AtomicInteger();

	private SparseArray<Intent> pendingActivities = new SparseArray<Intent>();

	private Application application;

	SFServiceHelper(Application app) {
		this.application = app;
	}

	public void addListener(SFServiceCallbackListener currentListener) {
		currentListeners.add(currentListener);
	}

	public void removeListener(SFServiceCallbackListener currentListener) {
		currentListeners.remove(currentListener);
	}

	// =========================================

	public int exampleActionLowPriority(String argumentA, String argumentB) {
		Log.d("Test","PREPARE COMMAND WITH LOW PRIORITY");
		final int requestId = createId();
		Intent i = buildTaskWithLowPriorityIntent(application, requestId, new ConcatenateCommand(argumentA,argumentB));
		return runRequest(requestId, i);
	}
	
	public int exampleActionLowestPriority(String argumentA, String argumentB) {
		Log.d("Test","PREPARE COMMAND WITH  LOWest PRIORITY");
		final int requestId = createId();
		Intent i = buildTaskWithLowestPriorityIntent(application, requestId, new ConcatenateCommand(argumentA,argumentB));
		return runRequest(requestId, i);
	}
	public int exampleActionHighPriority(String argumentA, String argumentB) {
		Log.d("Test","PREPARE COMMAND WITH HIGH PRIORITY");
		final int requestId = createId();
		Intent i = buildTaskWithHighPriorityIntent(application, requestId, new ConcatenateCommand(argumentA,argumentB));
		return runRequest(requestId, i);
	}
	
	public int exampleActionExtraHighPriority(String argumentA, String argumentB) {
		Log.d("Test","PREPARE COMMAND WITH EXTRA HIGH PRIORITY");
		final int requestId = createId();
		Intent i = buildTaskWithExtraHighPriorityIntent(application, requestId, new ConcatenateCommand(argumentA,argumentB));
		return runRequest(requestId, i);
	}
	public int exampleActionNormalPriority(String argumentA, String argumentB) {
		Log.d("Test","PREPARE COMMAND WITH NORMAL PRIORITY");
		final int requestId = createId();
		Intent i = buildTaskWithNormalPriorityIntent(application, requestId, new ConcatenateCommand(argumentA,argumentB));
		return runRequest(requestId, i);
	}

	// =========================================

	public void cancelCommand(int requestId) {
		Intent i = new Intent(application, CommandExecutorService.class);
		i.setAction(CommandExecutorService.ACTION_CANCEL_COMMAND);
		i.putExtra(TaskIntentPucker.EXTRA_REQUEST_ID, requestId);

		application.startService(i);
		pendingActivities.remove(requestId);
	}

	public boolean isPending(int requestId) {
		return pendingActivities.get(requestId) != null;
	}

	public boolean check(Intent intent, Class<? extends BaseCommand<?>> clazz) {
		Serializable commandExtra = new TaskIntentPucker(intent).getCommand();
		return commandExtra != null && commandExtra.getClass().equals(clazz);
	}

	private int createId() {
		return idCounter.getAndIncrement();
	}

	private int runRequest(final int requestId, Intent i) {
		pendingActivities.append(requestId, i);
		application.startService(i);
		return requestId;
	}
 
	private Intent buildTaskWithNormalPriorityIntent(final Context context,final int requestId,BaseCommand<?> command){
		return createIntent(context, requestId, command, ComparableFutureTask.NORMAL_PRIORITY);
	}
	
	private Intent buildTaskWithHighPriorityIntent(final Context context,final int requestId,BaseCommand<?> command){
		return createIntent(context, requestId, command, ComparableFutureTask.HIGH_PRIORITY);
	}
	private Intent buildTaskWithExtraHighPriorityIntent(final Context context,final int requestId,BaseCommand<?> command){
		return createIntent(context, requestId, command, ComparableFutureTask.EXTRA_HIGH_PRIORITY);
	}
	private Intent buildTaskWithLowPriorityIntent(final Context context,final int requestId,BaseCommand<?> command){
		return createIntent(context, requestId, command, ComparableFutureTask.LOW_PRIORITY);
	}
	private Intent buildTaskWithLowestPriorityIntent(final Context context,final int requestId,BaseCommand<?> command){
		return createIntent(context, requestId, command, ComparableFutureTask.LOWEST_PRIORITY);
	}
	
	private Intent createIntent(final Context context,final int requestId,BaseCommand<?> command,int priority) {
		Intent taskIntentForExecutor = new Intent(context, CommandExecutorService.class);
		taskIntentForExecutor.setAction(CommandExecutable.ACTION_EXECUTE_COMMAND);
		
		ResultReceiver callback=new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode,Bundle resultData) {
				Intent originalIntent = pendingActivities.get(requestId);
				if (isPending(requestId)) {
					if (resultCode != NotifySubscriberUtil.RESPONSE_PROGRESS) {
						pendingActivities.remove(requestId);
					}

					for (SFServiceCallbackListener currentListener : currentListeners) {
						if (currentListener != null) {
							currentListener.onServiceCallback(requestId, originalIntent,resultCode, resultData);
						}
					}
				}
			}
		};
		
		TaskIntentPucker taskIntentPucker=new TaskIntentPucker(taskIntentForExecutor);
		taskIntentPucker.puckData(requestId, callback, command, priority);
		return taskIntentForExecutor;
	}

}
