package com.iflytek.facedemo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.iflytek.facedemo.main.yanzhengsp;

/**
 * Created by Administrator on 2016/4/6.
 */
public class start extends Activity implements View.OnClickListener{
    private Button addbt;
    private Button loginbt;
    private ImageView im;
    private ImageView ig;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);
        addbt = (Button) findViewById(R.id.add_button);
        loginbt = (Button) findViewById(R.id.login_button);
        im = (ImageView) findViewById(R.id.logoiv);
        ig=(ImageView) findViewById(R.id.wenzizi);
        addbt.setOnClickListener(this);
        loginbt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.add_button:
                Intent i = new Intent(start.this,register.class);
                startActivity(i);
                break;
            case R.id.login_button:
                Intent j = new Intent(start.this,yanzhengsp.class);
                startActivity(j);
                break;
            default:
                break;
        }
    }
}
