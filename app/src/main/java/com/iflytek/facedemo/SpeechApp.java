package com.iflytek.facedemo;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.iflytek.cloud.SpeechUtility;

public class SpeechApp extends Application {
	@Override
	public void onCreate() {
		SpeechUtility.createUtility(SpeechApp.this, "appid=" + getString(R.string.app_id));
		super.onCreate();
		AVOSCloud.setDebugLogEnabled(true);

	}
}
