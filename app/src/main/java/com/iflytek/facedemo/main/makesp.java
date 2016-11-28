package com.iflytek.facedemo.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.iflytek.facedemo.R;
import com.iflytek.facedemo.start;
import com.iflytek.facedemo.view.LockPattern;
import java.util.List;


public class makesp extends Activity
{
    private LockPattern mLockPattern;
    private SharedPreferences sp;
    private TextView tv,tv1,tv2;
    private String mAuthid1;
    private String mname;
    private String jlps ;//password
    private Button make_button;
    AVObject u = new AVObject("MyUser");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_makesp);
        make_button=(Button)findViewById(R.id.make_button);
        mLockPattern = (LockPattern) findViewById(R.id.lockPattern9);
        AssetManager manager=getAssets();
        Typeface tf1=Typeface.createFromAsset(manager, "fonts/hhh.ttf");
        tv=(TextView)findViewById(R.id.tv);
        tv1=(TextView)findViewById(R.id.tv1);
        tv2=(TextView)findViewById(R.id.tv2);
        tv.setTypeface(tf1);
        tv1.setTypeface(tf1);
        tv2.setTypeface(tf1);
        Intent i = getIntent();
        Bundle date = i.getExtras();
        mAuthid1 = date.getString("id");
        mname = date.getString("name");

        mLockPattern.setOnCompleteListener(new LockPattern.OnCompleteListener() {
            public void onPwdShortOrLong(int pwdLength) {
                Toast.makeText(makesp.this, "密码太短啦", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(String password) {
                jlps = password;
                Log.d("tag", password);
                Toast.makeText(makesp.this, "制定成功", Toast.LENGTH_SHORT).show();

                // 请根据需求使用加密工具
                // password = MD5Utils.encode(password);
            }

            public void changeview() {
                AssetManager manager=getAssets();
                Typeface tf=Typeface.createFromAsset(manager, "fonts/hhh.ttf");
                tv.setText("松手后完成制定");
                tv.setTypeface(tf);
            }
        });

            make_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AVQuery<AVObject> query = new AVQuery<AVObject>("MyUser");
                    query.whereEqualTo("id", mAuthid1);
                    query.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            if (e == null) {//服务器成功交互
                                if (list.isEmpty() == true) {
                                    if(jlps == null){
                                        Toast.makeText(makesp.this, "password不能为空", Toast.LENGTH_LONG).show();
                                    }
                                   else {
                                        u.put("id", mAuthid1);
                                        u.put("str", jlps);//这里的变量要求必须是makesp类的数据成员，
                                        u.put("name", mname);//否则不可见
                                        u.saveInBackground();
                                        Intent q = new Intent(makesp.this, start.class);
                                        startActivity(q);
                                    }
                                } else {
                                    Toast.makeText(makesp.this, "已经有这个人", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });

                }
            });
    }
}
