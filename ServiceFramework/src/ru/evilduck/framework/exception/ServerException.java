package ru.evilduck.framework.exception;

@SuppressWarnings("serial")
public class ServerException extends Exception{

	public ServerException(String message) {
		super(message);
	}
}