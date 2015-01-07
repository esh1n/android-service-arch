/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.evilduck.framework.handlers.implemetation;

import ru.evilduck.framework.handlers.BaseCommand;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 *
 * @author sergey
 */

@SuppressWarnings("serial")
public class ConcatenateCommand extends BaseCommand<String>{

    private String firstPart;
    private String secondPart;
    public ConcatenateCommand(String firstPart,String secondPart){
        this.firstPart=firstPart;
        this.secondPart=secondPart;
    }

    @Override
    public String execute(Context context) {
    	if (TextUtils.isEmpty(firstPart) || TextUtils.isEmpty(secondPart)) {
    		Log.d("Test", "return fail");
			return "Surprise!";
		} else {
			Log.d("Test", "return result :"+firstPart + secondPart);
			return firstPart + secondPart;
		}
    }
   
    
}