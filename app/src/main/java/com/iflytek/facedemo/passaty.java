package com.iflytek.facedemo;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.iflytek.facedemo.view.MyGifView;

public class passaty extends Activity {
    private String name;
    private TextView showname;
    private Button tiaochu;
    private TextView time;
    private int i = 10;
    private String j ;
    private int k;
    private Timer timer = null;
    private TimerTask task = null;
    MyGifView mg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_passaty);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        name = b.getString("name");
        mg=(MyGifView)findViewById(R.id.image);
        showname = (TextView) findViewById(R.id.showname);
        tiaochu=(Button)findViewById(R.id.tiaochu);
        time = (TextView) findViewById(R.id.time);

        AssetManager managerr=getAssets();
        Typeface tfg=Typeface.createFromAsset(managerr,"fonts/ll.TTF");
        showname.setTypeface(tfg);
        startTime();
        mg.setMovieResource(R.drawable.xiangjiao);
        AssetManager manager=getAssets();
        Typeface tf=Typeface.createFromAsset(manager,"fonts/hhh.ttf");
        showname.setTypeface(tf);
        showname.setText("yourname:    " + name);
        tiaochu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(passaty.this,start.class);
                startActivity(i);
            }
        });

    }private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            AssetManager manager=getAssets();
            Typeface tf=Typeface.createFromAsset(manager,"fonts/hhh.ttf");
            time.setTypeface(tf);
            time.setText(msg.arg1+"");
            startTime();
            j = time.getText().toString();
            k = Integer.parseInt(j);
            if(k == 0){
                stopTime();
                Intent i=new Intent(passaty.this,start.class);
                startActivity(i);
            }
        };
    };

    public void startTime(){
        timer = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
                i--;
                Message  message = mHandler.obtainMessage();
                message.arg1 = i;
                mHandler.sendMessage(message);

            }
        };
        timer.schedule(task, 1000);
    }

    public void stopTime(){
        timer.cancel();
    }

}
