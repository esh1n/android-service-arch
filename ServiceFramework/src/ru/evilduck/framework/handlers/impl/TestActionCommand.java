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
package ru.evilduck.framework.handlers.impl;

import java.util.Random;

import ru.evilduck.framework.handlers.SFBaseCommand;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

public class TestActionCommand extends SFBaseCommand {

	private static final String TAG = "TestActionCommand";

	private String arg1;
	private String arg2;

	public TestActionCommand(String arg1, String arg2) {
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
	
	@Override
	public void doExecute(Intent intent, Context context,
			ResultReceiver callback) {
		Bundle data = new Bundle();

		Random rnd = new Random();

		int progress = 0;
		sendProgress(progress);
		

			while (progress < 100) {
				int mills=rnd.nextInt(300) + 200;
				Log.d(TAG, "before sleep "+mills);
				Log.d(TAG, "after sleep "+mills);
				if (cancelled) {
					Log.w(TAG, "Command was cancelled");
					return;
				}

				progress += rnd.nextInt(40);
				if (progress > 100) {
					progress = 100;
				}
				sendProgress(progress);
			}
		

		if (TextUtils.isEmpty(arg1) || TextUtils.isEmpty(arg2)) {
			data.putString("error", "Surprise!");
			notifyFailure(data);
		} else {
			data.putString("data", arg1 + arg2);
			notifySuccess(data);
		}
	}

	
	

}
