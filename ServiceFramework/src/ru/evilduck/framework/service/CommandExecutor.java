package ru.evilduck.framework.service;

import java.io.Serializable;

import ru.evilduck.framework.SFApplication;
import ru.evilduck.framework.handlers.SFBaseCommand;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class CommandExecutor {
	public static final int RESPONSE_SUCCESS = 0;

	public static final int RESPONSE_FAILURE = 1;

	public static final int RESPONSE_PROGRESS = 2;
	public static final String EXTRA_RESULT = "RESULT";
	public static final String ERROR_CODE = "CODE";
	public static String EXTRA_PROGRESS = SFApplication.PACKAGE.concat(".EXTRA_PROGRESS");

	public static void executeCommand(Context context, SFBaseCommand<?> command,Intent intent,ResultReceiver callback) {
		try {
			Serializable result = command.execute(context);
			if (result != null) {
				
				notifySuccess(result, callback);
			}
			else{
				notifyNullResult(callback);
			}
		}
		
		catch (Exception e) {
			Bundle errorBundle=ErrorProcessor.getExceptionBundle(e, context);
			notifyFailure(errorBundle, callback);
			e.printStackTrace();
		} 
		 /*
		  * catch (ServerException e) {
			SLog.d("ARCH","ServerException");
			Bundle errorBundle=ErrorProcessor.getExceptionBundle(e, context);
			notifyFailure(errorBundle, callback);
			e.printStackTrace();
		} catch (AuthenticationException e) {
			SLog.d("ARCH","AuthenticationException");
			Bundle errorBundle=ErrorProcessor.getExceptionBundle(e, context);
			notifyFailure(errorBundle, callback);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			SLog.d("ARCH","FileNotFoundException");
			Bundle errorBundle=ErrorProcessor.getExceptionBundle(e, context);
			notifyFailure(errorBundle, callback);
			e.printStackTrace();
		} catch (IOException e) {
			SLog.d("ARCH","IOException");
			Bundle errorBundle=ErrorProcessor.getExceptionBundle(e, context);
			notifyFailure(errorBundle, callback);
			e.printStackTrace();
		} catch (SQLException e) {
			SLog.d("ARCH","SQLException");
			Bundle errorBundle=ErrorProcessor.getExceptionBundle(e, context);
			notifyFailure(errorBundle, callback);
			e.printStackTrace();
		} 
		  * */
		
		
	}
	
	protected static void notifySuccess(Serializable result, ResultReceiver sfCallback) {
		Bundle sendBundle=new Bundle();
		sendBundle.putSerializable(EXTRA_RESULT,  result);
		sendUpdate(RESPONSE_SUCCESS, sendBundle,sfCallback);
	}

	private static void  notifyFailure(Bundle errorBundle,ResultReceiver sfCallback) {
		sendUpdate(RESPONSE_FAILURE, errorBundle,sfCallback );
	}
	private static void notifyNullResult(ResultReceiver callback) {
		Bundle bundle = new Bundle();
		bundle.putInt(ERROR_CODE,  ErrorProcessor.RESULT_NULL_ERROR);
		notifyFailure(bundle, callback);
	}

	private static void sendProgress(Bundle data,int progress,ResultReceiver sfCallback) {
		data.putInt(EXTRA_PROGRESS, progress);
		sendUpdate(RESPONSE_PROGRESS, data,sfCallback);
	}

	private static void sendUpdate(int resultCode, Bundle data,ResultReceiver sfCallback) {
		if (sfCallback != null) {
			sfCallback.send(resultCode, data);
		}
	}
}