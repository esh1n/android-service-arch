package ru.evilduck.framework.service;

import android.content.Intent;
import android.os.ResultReceiver;
import ru.evilduck.framework.SFApplication;
import ru.evilduck.framework.armedthreadpool.wrapper.CallableCommandWrapper;
import ru.evilduck.framework.armedthreadpool.wrapper.ComparableFutureTask;
import ru.evilduck.framework.armedthreadpool.wrapper.RunningTask;
import ru.evilduck.framework.armedthreadpool.wrapper.RunningTaskWithPriority;
import ru.evilduck.framework.handlers.BaseCommand;

public class TaskIntentPucker {
	

	public static final String EXTRA_REQUEST_ID = SFApplication.PACKAGE.concat(".EXTRA_REQUEST_ID");

	public static final String EXTRA_STATUS_RECEIVER = SFApplication.PACKAGE.concat(".STATUS_RECEIVER");

	public static final String EXTRA_COMMAND = SFApplication.PACKAGE.concat(".EXTRA_COMMAND");
	
	public static final String EXTRA_COMMAND_PRIORITY = SFApplication.PACKAGE.concat(".EXTRA_COMMAND_PRIORITY");

	private Intent intent;

	public TaskIntentPucker(){
	}
	public TaskIntentPucker(Intent intent){
		this.intent=intent;
	}
	
	public void replaceIntent(Intent newIntent){
		this.intent=newIntent;
	}
	public  ResultReceiver getReceiver() {
		return intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
	}
	public  BaseCommand getCommand() {
		return (BaseCommand) intent.getSerializableExtra(EXTRA_COMMAND);
	}
	public  int getCommandId() {
		return intent.getIntExtra(EXTRA_REQUEST_ID, -1);
	}
	public  int getPriority() {
		return intent.getIntExtra(EXTRA_COMMAND_PRIORITY, ComparableFutureTask.NORMAL_PRIORITY);
	}
	public  void puckData(final int requestId,ResultReceiver callback,BaseCommand<?> command,int priority){
		intent.putExtra(EXTRA_COMMAND, command);
		intent.putExtra(EXTRA_REQUEST_ID, requestId);
		intent.putExtra(EXTRA_STATUS_RECEIVER,callback);
		intent.putExtra(EXTRA_COMMAND_PRIORITY, priority);
	}
}
