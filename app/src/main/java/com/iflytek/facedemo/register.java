package com.iflytek.facedemo;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iflytek.facedemo.main.FaceDemo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class register extends Activity implements View.OnClickListener {
    private Toast mToast;
    private String name;
    private String id;
    private EditText idtext;
    private String j="hello";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        AVOSCloud.initialize(this, "rll1Okai77GFdJsIPPIP35ki-gzGzoHsz", "f621DpypKIbFFFrBetKG2jTM");
        AVOSCloud.setDebugLogEnabled(true);
        setContentView(R.layout.activity_register);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        final TextView t = (TextView) findViewById(R.id.notice);
        AssetManager manager = getAssets();
        Typeface tf = Typeface.createFromAsset(manager, "fonts/english.TTF");
        t.setTypeface(tf);
        t.setText("Please input your name and id");
        findViewById(R.id.submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                name = ((EditText) findViewById(R.id.nameput)).getText().toString();
                id = ((EditText) findViewById(R.id.idput)).getText().toString();
                if (TextUtils.isEmpty(name) && TextUtils.isEmpty(id)) {
                    showTip("用户名或者ID不能为空");
                    YoYo.with(Techniques.Tada)
                            .duration(700)
                            .playOn(findViewById(R.id.edit_area));
                    return;
                } else {
                    Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
                    Matcher m = p.matcher(id);
                    if (m.find()) {
                        showTip("id不支持中文字符");
                        YoYo.with(Techniques.Tada)
                                .duration(700)
                                .playOn(findViewById(R.id.edit_area));
                        return;
                    } else if (id.contains(" ")) {
                        showTip("id不能包含空格");
                        YoYo.with(Techniques.Tada)
                                .duration(700)
                                .playOn(findViewById(R.id.edit_area));
                        return;
                    } else if (!id.matches("^[a-zA-Z][a-zA-Z0-9_]{5,17}")) {
                        showTip("id由6-18个字母、数字或下划线的组合，以字母开头");
                        YoYo.with(Techniques.Tada)
                                .duration(700)
                                .playOn(findViewById(R.id.edit_area));
                        return;
                    }

                    }
                idtext=(EditText)findViewById(R.id.idput);
                AVQuery<AVObject> query = new AVQuery<AVObject>("MyUser");
                query.whereEqualTo("id", id);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {//服务器成功交互
                                j = "world";

                                Toast.makeText(register.this, "已经有这个人", Toast.LENGTH_LONG).show();
                                idtext.getText().clear();
                        }
                    }
                });
                Toast.makeText(register.this,j, Toast.LENGTH_LONG).show();
                if(j.equals("hello")){
                    Intent i = new Intent(register.this,FaceDemo.class);
                    Bundle b = new Bundle();
                    b.putString("id", id);
                    b.putString("name", name);
                    b.putInt("pd",1);
                    i.putExtras(b);
                    startActivity(i);
                }
                /*
                Intent i = new Intent(register.this,FaceDemo.class);
                Bundle b = new Bundle();
                b.putString("id", id);
                b.putString("name", name);
                b.putInt("pd",1);
                i.putExtras(b);
                startActivity(i);*/

        }
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
    }
