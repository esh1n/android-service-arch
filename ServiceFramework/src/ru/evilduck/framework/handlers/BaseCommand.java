package ru.evilduck.framework.handlers;

import java.io.Serializable;

import android.content.Context;


@SuppressWarnings("serial")
public abstract class BaseCommand<T extends Serializable> implements Serializable {

	public abstract T execute(Context context) throws  Exception;
	
}