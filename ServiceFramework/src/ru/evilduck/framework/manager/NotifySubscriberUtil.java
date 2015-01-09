package ru.evilduck.framework.manager;

import java.io.Serializable;

import ru.evilduck.framework.SFApplication;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class NotifySubscriberUtil {
	public static final int RESPONSE_SUCCESS = 0;

	public static final int RESPONSE_FAILURE = 1;

	public static final int RESPONSE_PROGRESS = 2;
	public static final String EXTRA_RESULT = "RESULT";
	public static final String ERROR_CODE = "CODE";
	public static final String EXTRA_PROGRESS = "PROGRESS";

	
	public static void notifySuccess(Serializable result, ResultReceiver sfCallback) {
		Log.d("Test", "notifySuccess with Result "+result);
		Bundle sendBundle=new Bundle();
		sendBundle.putSerializable(EXTRA_RESULT,  result);
		sendUpdate(RESPONSE_SUCCESS, sendBundle,sfCallback);
	}

	public static void  notifyFailure(Bundle errorBundle,ResultReceiver sfCallback) {
		Log.d("Test", "notifyFailure ");
		sendUpdate(RESPONSE_FAILURE, errorBundle,sfCallback );
	}
	public static void notifyNullResult(ResultReceiver callback) {
		Bundle bundle = new Bundle();
		bundle.putInt(ERROR_CODE,  ErrorProcessor.RESULT_NULL_ERROR);
		notifyFailure(bundle, callback);
	}

	private static void sendProgress(int progress,ResultReceiver sfCallback) {
		Bundle sendedData = new Bundle();
		sendedData.putInt(EXTRA_PROGRESS, progress);
		sendUpdate(RESPONSE_PROGRESS, sendedData,sfCallback);
	}

	private static void sendUpdate(int resultCode, Bundle data,ResultReceiver sfCallback) {
		if (sfCallback != null) {
			sfCallback.send(resultCode, data);
		}
	}
	public static void notifyDoneCommand(Serializable commandResult, ResultReceiver sfCallback) {
		Bundle sendBundle=new Bundle();
		int resultOperationCode=0;
		if(commandResult!=null){
			resultOperationCode=RESPONSE_SUCCESS;
			sendBundle.putSerializable(EXTRA_RESULT,  commandResult);
		}
		else{
			resultOperationCode=RESPONSE_FAILURE;
			sendBundle.putInt(ERROR_CODE,  ErrorProcessor.RESULT_NULL_ERROR);
		}
		sendUpdate(resultOperationCode, sendBundle, sfCallback);
		
	}
}