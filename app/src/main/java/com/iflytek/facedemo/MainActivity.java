package com.iflytek.facedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import com.avos.avoscloud.AVOSCloud;


/**
 * Created by Administrator on 2016/4/8.
 */
public class MainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		//延迟两秒后执行run方法中的页面跳转
		AVOSCloud.initialize(this, "rll1Okai77GFdJsIPPIP35ki-gzGzoHsz", "f621DpypKIbFFFrBetKG2jTM");

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
				startActivity(intent);
				MainActivity.this.finish();
			}
		}, 2000);
	}
}
