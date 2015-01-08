package ru.evilduck.framework.service;

import android.app.IntentService;
import android.content.Intent;

public class SimpleExecutorService extends IntentService{

	private static final String TAG="SimpleExecutorService";
	
	public SimpleExecutorService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
	}

}
