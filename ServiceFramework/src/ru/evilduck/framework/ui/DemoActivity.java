/*
 * Copyright (C) 2013 Alexander Osmanov (http://perfectear.educkapps.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ru.evilduck.framework.ui;

import ru.evilduck.framework.R;
import ru.evilduck.framework.SFBaseActivity;
import ru.evilduck.framework.handlers.implemetation.ConcatenateCommand;
import ru.evilduck.framework.service.NotifySubscriberUtil;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class DemoActivity extends SFBaseActivity {

	private static final String PROGRESS_DIALOG = "progress-dialog";

	private EditText text1;

	private EditText text2;

	private int highPriorityRequestId = -1;
	private int normalPriorityRequestId = -1;
	private int dummyNormalPriorityRequestId1 = -1;
	private int dummyNormalPriorityRequestId2 = -1;
	private int transactionalRequestId1 = -3;
	private int transactionalRequestId2 = -4;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		text1 = (EditText) findViewById(R.id.editText1);
		text2 = (EditText) findViewById(R.id.editText2);
		text2.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent event) {
				if (id == EditorInfo.IME_ACTION_DONE) {
					doIt();
				}
				return false;
			}
		});

		findViewById(R.id.button_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						doIt();
					}

				});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Processing");
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				getServiceHelper().cancelCommand(highPriorityRequestId);
				getServiceHelper().cancelCommand(normalPriorityRequestId);
			}
		});

		return progressDialog;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (highPriorityRequestId != -1 && !getServiceHelper().isPending(highPriorityRequestId)) {
			dismissProgressDialog();
		}
		if (normalPriorityRequestId != -1 && !getServiceHelper().isPending(normalPriorityRequestId)) {
			dismissProgressDialog();
		}
	}

	private void doIt() {
		ProgressDialogFragment progress = new ProgressDialogFragment();
		progress.show(getSupportFragmentManager(), PROGRESS_DIALOG);
        Log.d("Test","START TWO COMMANDS");
        dummyNormalPriorityRequestId1 = getServiceHelper().exampleActionNormalPriority("NORMAL1 ".concat(text1.getText().toString()), text2.getText().toString());
        normalPriorityRequestId = getServiceHelper().exampleActionNormalPriority("NORMAL2 ".concat(text1.getText().toString()), text2.getText().toString());
		highPriorityRequestId = getServiceHelper().exampleActionHighPriority("HIGH ".concat(text1.getText().toString()), text2.getText().toString());
		transactionalRequestId1 = getServiceHelper().exampleActionTrancsactional("TRANCSACT1 ".concat(text1.getText().toString()), text2.getText().toString());
		transactionalRequestId2 = getServiceHelper().exampleActionTrancsactional("TRANCSACT2 ".concat(text1.getText().toString()), text2.getText().toString());
		
	}

	@Override
	public void onServiceCallback(int requestId, Intent requestIntent,int resultCode, Bundle resultData) {
		super.onServiceCallback(requestId, requestIntent, resultCode,resultData);
		 Log.e("Test","GOT RESULTS OF TWO COMMANDS");
		if (getServiceHelper().check(requestIntent, ConcatenateCommand.class)) {
			if (resultCode == NotifySubscriberUtil.RESPONSE_SUCCESS) {
				Toast.makeText(this, resultData.getString(NotifySubscriberUtil.EXTRA_RESULT),Toast.LENGTH_LONG).show();
				dismissProgressDialog();
			} else if (resultCode == NotifySubscriberUtil.RESPONSE_PROGRESS) {
				updateProgressDialog(resultData.getInt(NotifySubscriberUtil.EXTRA_PROGRESS, -1));
			} else {
				Toast.makeText(this, resultData.getString("error"),Toast.LENGTH_LONG).show();
				dismissProgressDialog();
			}
		}
	}

	public void cancelCommand() {
		getServiceHelper().cancelCommand(highPriorityRequestId);
		getServiceHelper().cancelCommand(normalPriorityRequestId);
	}

	public static class ProgressDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			ProgressDialog progressDialog = new ProgressDialog(getActivity());
			progressDialog.setMessage("Result: 0%");

			return progressDialog;
		}

		public void setProgress(int progress) {
			((ProgressDialog) getDialog()).setMessage("Result: " + progress
					+ "%");
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			super.onCancel(dialog);
			((DemoActivity) getActivity()).cancelCommand();
		}

	}

	private void dismissProgressDialog() {
		ProgressDialogFragment progress = (ProgressDialogFragment) getSupportFragmentManager()
				.findFragmentByTag(PROGRESS_DIALOG);
		if (progress != null) {
			progress.dismiss();
		}
	}

	private void updateProgressDialog(int progress) {
		ProgressDialogFragment progressDialog = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(PROGRESS_DIALOG);
		if (progressDialog != null) {
			progressDialog.setProgress(progress);
		}
	}

}