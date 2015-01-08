package ru.evilduck.framework.service.interfaces;

import android.content.Intent;
import ru.evilduck.framework.SFApplication;
import ru.evilduck.framework.armedthreadpool.wrapper.RunningTask;

public interface CommandExecutable {

	public static final String ACTION_EXECUTE_COMMAND = SFApplication.PACKAGE.concat(".ACTION_EXECUTE_COMMAND");
	
	public static final String ACTION_CANCEL_COMMAND = SFApplication.PACKAGE.concat(".ACTION_CANCEL_COMMAND");
	
	RunningTask unpuckIntentToTask();
}
