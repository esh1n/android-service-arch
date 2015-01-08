package ru.evilduck.framework.service;

import ru.evilduck.framework.SFApplication;

public interface CommandExecutable {
	public static final String ACTION_EXECUTE_COMMAND = SFApplication.PACKAGE.concat(".ACTION_EXECUTE_COMMAND");

	public static final String ACTION_CANCEL_COMMAND = SFApplication.PACKAGE.concat(".ACTION_CANCEL_COMMAND");

	public static final String EXTRA_REQUEST_ID = SFApplication.PACKAGE.concat(".EXTRA_REQUEST_ID");

	public static final String EXTRA_STATUS_RECEIVER = SFApplication.PACKAGE.concat(".STATUS_RECEIVER");

	public static final String EXTRA_COMMAND = SFApplication.PACKAGE.concat(".EXTRA_COMMAND");
}
