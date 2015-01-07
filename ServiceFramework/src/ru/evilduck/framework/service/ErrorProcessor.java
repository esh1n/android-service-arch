package ru.evilduck.framework.service;

import ru.evilduck.framework.R;
import ru.evilduck.framework.exception.InternetConnectionException;
import ru.evilduck.framework.exception.ServerException;
import ru.evilduck.framework.util.UiUtil;
import android.content.Context;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;

public class ErrorProcessor {

	/** general errors */
	public static final int OK = 0;
	public static final int DATA_BASE_ERROR = 1;
	public static final int SERVER_ERROR = 2;
	public static final int AUTHENTICATION_ERROR = 3;
	public static final int CONNECTION_ERROR = 4;
	public static final int RESULT_NULL_ERROR = 5;

	/** custom errors */
	public static final int TRANSPORT_ERROR = 6;
	public static final int BUSINESS_ERROR = 7;

	public static final int CODE_OK = 0;
	public static final int CODE_BAD_REQUEST = 690;
	public static final int CODE_SERVER_ERROR = 691;
	public static final int SEND_MAIL_ERROR = 693;
	public static final int SEND_MAIL__AUTH_ERROR = 694;

	private static final int FAKE_STRING_RES_ID = -1;

	public static Bundle getExceptionBundle(Exception exception) {
		Bundle bundle = new Bundle();
		if (exception instanceof InternetConnectionException||exception.getCause() instanceof InternetConnectionException) {
			Log.d("ARCH","CONNECTION_ERROR" +exception.getClass());
			bundle.putInt(NotifySubscriberUtil.ERROR_CODE, CONNECTION_ERROR);
		}
		if (exception instanceof SQLException || exception.getCause() instanceof SQLException) {
			bundle.putInt(NotifySubscriberUtil.ERROR_CODE, DATA_BASE_ERROR);
		}
		if(exception instanceof ServerException || exception.getCause() instanceof ServerException){
			bundle.putInt(NotifySubscriberUtil.ERROR_CODE, SERVER_ERROR);
		}
		return bundle;
	}
	public static Bundle getExceptionBundle(Throwable exception) {
		Bundle bundle = new Bundle();
		if (exception instanceof InternetConnectionException||exception.getCause() instanceof InternetConnectionException) {
			Log.d("ARCH","CONNECTION_ERROR" +exception.getClass());
			bundle.putInt(NotifySubscriberUtil.ERROR_CODE, CONNECTION_ERROR);
		}
		if (exception instanceof SQLException || exception.getCause() instanceof SQLException) {
			bundle.putInt(NotifySubscriberUtil.ERROR_CODE, DATA_BASE_ERROR);
		}
		if(exception instanceof ServerException || exception.getCause() instanceof ServerException){
			bundle.putInt(NotifySubscriberUtil.ERROR_CODE, SERVER_ERROR);
		}
		return bundle;
	}

	public static void processError(Bundle bundle, Context context) {
		int stringResourseId = FAKE_STRING_RES_ID;
		switch (bundle.getInt(NotifySubscriberUtil.ERROR_CODE)) {
		case DATA_BASE_ERROR:
			stringResourseId = R.string.error_database;
			break;

		case SERVER_ERROR:
			 stringResourseId = R.string.error_server_toast;
				break;
		case AUTHENTICATION_ERROR:
			 stringResourseId = R.string.error_server_toast;
			break;

		case CONNECTION_ERROR:
			stringResourseId = R.string.error_no_internet_connection;
		case RESULT_NULL_ERROR: {
			break;
		}
		case TRANSPORT_ERROR:
			// stringResourseId = R.string.error_connection_toast;
			break;

		case BUSINESS_ERROR:

			break;
		default:
			 stringResourseId = R.string.common_error_unexpected;
			break;
		}
		if (stringResourseId != FAKE_STRING_RES_ID) {
			UiUtil.showToast(context, stringResourseId);
		}
		else{
			Log.d("ARCH","FAKE_STRING_RES_ID");
		}

	}

}
