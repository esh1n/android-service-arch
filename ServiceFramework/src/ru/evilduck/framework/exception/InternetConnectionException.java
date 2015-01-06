package ru.evilduck.framework.exception;

@SuppressWarnings("serial")
public class InternetConnectionException extends Exception{

	public InternetConnectionException(String message) {
		super(message);
	}
}
