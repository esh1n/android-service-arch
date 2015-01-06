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

import ru.evilduck.framework.handlers.SFBaseCommand;
import android.content.Context;
import android.text.TextUtils;

@SuppressWarnings("serial")
public class TestActionCommand extends SFBaseCommand<String> {

	private static final String TAG = "TestActionCommand";

	private String arg1;
	private String arg2;

	public TestActionCommand(String arg1, String arg2) {
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	@Override
	public String execute(Context context) {
		if (TextUtils.isEmpty(arg1) || TextUtils.isEmpty(arg2)) {
			return "Surprise!";
		} else {
			return arg1 + arg2;
		}
	}

}
