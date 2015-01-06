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
import java.util.concurrent.Executors;

import ru.evilduck.framework.SFApplication;
import ru.evilduck.framework.handlers.SFBaseCommand;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.ResultReceiver;
import android.util.Log;

public class SFCommandExecutorService extends Service {

	private static final int NUM_THREADS_OF_PARALLEL_EXECUTOR = 2;

	public static final String ACTION_EXECUTE_COMMAND = SFApplication.PACKAGE
			.concat(".ACTION_EXECUTE_COMMAND");

	public static final String ACTION_CANCEL_COMMAND = SFApplication.PACKAGE
			.concat(".ACTION_CANCEL_COMMAND");

	public static final String EXTRA_REQUEST_ID = SFApplication.PACKAGE
			.concat(".EXTRA_REQUEST_ID");

	public static final String EXTRA_STATUS_RECEIVER = SFApplication.PACKAGE
			.concat(".STATUS_RECEIVER");

	public static final String EXTRA_COMMAND = SFApplication.PACKAGE
			.concat(".EXTRA_COMMAND");

	private ExecutorService executorParallel = Executors
			.newFixedThreadPool(NUM_THREADS_OF_PARALLEL_EXECUTOR);

	private ExecutorService executorTransactional = Executors
			.newSingleThreadExecutor();

	private ConcurrentHashMap<Integer, RunningCommand> runningCommands = new ConcurrentHashMap<Integer, RunningCommand>();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		executorTransactional.shutdownNow();
		executorParallel.shutdownNow();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Working", "onStartCommand");
		if (ACTION_EXECUTE_COMMAND.equals(intent.getAction())) {
			RunningCommand runningCommand = new RunningCommand(intent);
			runningCommands.put(getCommandId(intent), runningCommand);
			executorTransactional.submit(runningCommand);
		}
		if (ACTION_CANCEL_COMMAND.equals(intent.getAction())) {
			RunningCommand runningCommand = runningCommands
					.get(getCommandId(intent));
			if (runningCommand != null) {
				runningCommand.cancel();
			}
		}

		return START_NOT_STICKY;
	}

	private class RunningCommand implements Runnable {

		private Intent intent;

		private SFBaseCommand command;

		public RunningCommand(Intent intent) {
			this.intent = intent;

			command = getCommand(intent);
		}

		public void cancel() {
			command.cancel();
		}

		@Override
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			command.execute(intent, getApplicationContext(),getReceiver(intent));
			shutdown();
		}

		private void shutdown() {
			runningCommands.remove(getCommandId(intent));
			if (runningCommands.isEmpty()) {
				stopSelf();
			}
		}

	}

	private ResultReceiver getReceiver(Intent intent) {
		return intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
	}

	private SFBaseCommand getCommand(Intent intent) {
		return (SFBaseCommand) intent.getSerializableExtra(EXTRA_COMMAND);
	}

	private int getCommandId(Intent intent) {
		return intent.getIntExtra(EXTRA_REQUEST_ID, -1);
	}
    
	
}
