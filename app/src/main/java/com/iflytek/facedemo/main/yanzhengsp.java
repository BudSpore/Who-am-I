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
import com.iflytek.facedemo.view.LockPattern.OnCompleteListener;

import java.util.List;

public class yanzhengsp extends Activity
{
    private Button button;
    private LockPattern mLockPattern;
    private SharedPreferences sp;
    private TextView tv,tv1;
    AVObject u = new AVObject("MyUser");
    static String nam;
    static String Id;
    String nam1;
    String id1;
    private int pl=2;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_yanzhengsp);
        button=(Button)findViewById(R.id.yanzheng_bt);
        mLockPattern = (LockPattern) findViewById(R.id.lockPattern9);
        AssetManager manager=getAssets();
        Typeface tf1=Typeface.createFromAsset(manager, "fonts/hhh.ttf");
        tv=(TextView)findViewById(R.id.tv);
        tv1=(TextView)findViewById(R.id.tv3);
        tv.setTypeface(tf1);
        tv1.setTypeface(tf1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(yanzhengsp.this,start.class);
                startActivity(i);
                finish();

            }
        });
        mLockPattern.b=0;
        // 根据用户设置是否显示滑屏轨迹
        // mLockPattern.hideLocus();
        mLockPattern.setOnCompleteListener(new OnCompleteListener() {

            public void onPwdShortOrLong(int pwdLength) {
                Toast.makeText(yanzhengsp.this, "密码不正确", Toast.LENGTH_SHORT).show();
                mLockPattern.passwordError();
            }

            @Override
            public void onComplete(String password) {
                SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                Log.d("tag", password);
                String name = pref.getString("1", "");
                Log.d("tagg", name);
                //通过password找到对应的数据
                AVQuery<AVObject> query = new AVQuery<AVObject>("MyUser");
                query.whereEqualTo("str", password);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {//服务器成功交互
                            if (list.isEmpty() == false) {
                                nam = list.get(0).getString("name");
                                Id = list.get(0).getString("id");//通过password得到了id和name
                                preference(nam, Id);
                                Intent a = new Intent(yanzhengsp.this,FaceDemo.class);
                                Bundle b = new Bundle();
                                b.putString("name",nam);
                                b.putString("id",Id);
                                b.putInt("pd",pl);
                                a.putExtras(b);
                                startActivity(a);
                            } else {
                                button.setVisibility(View.VISIBLE);
                                Toast.makeText(yanzhengsp.this, "你是陌生人,请先注册", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                // 请根据需求使用加密工具
                // password = MD5Utils.encode(password);
            }

            public void preference(String nam, String Id) {
                nam1 = nam;
                id1 = Id;
            }

            public void changeview() {
                tv.setText("松手后完成验证");
            }
        });

    }
}
