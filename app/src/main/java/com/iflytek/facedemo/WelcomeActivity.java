package com.iflytek.facedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class WelcomeActivity extends Activity implements OnViewChangeListener{

    private MyScrollLayout mScrollLayout;
    private ImageView[] imgs;
    private int count;
    private int currentItem;
    private Button startBtn;
    private RelativeLayout mainRLayout;
    private LinearLayout pointLLayout;
    private LinearLayout leftLayout;
    private LinearLayout rightLayout;
    private LinearLayout animLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        initView();
    }

    private void initView() {
        mScrollLayout  = (MyScrollLayout) findViewById(R.id.ScrollLayout);
        pointLLayout = (LinearLayout) findViewById(R.id.llayout);
        mainRLayout = (RelativeLayout) findViewById(R.id.mainRLayout);
        startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setOnClickListener(onClick);
        animLayout = (LinearLayout) findViewById(R.id.animLayout);
        leftLayout  = (LinearLayout) findViewById(R.id.leftLayout);
        rightLayout  = (LinearLayout) findViewById(R.id.rightLayout);
        count = mScrollLayout.getChildCount();
        imgs = new ImageView[count];
        for(int i = 0; i< count;i++) {
            imgs[i] = (ImageView) pointLLayout.getChildAt(i);
            imgs[i].setEnabled(true);
            imgs[i].setTag(i);
        }
        currentItem = 0;
        imgs[currentItem].setEnabled(false);
        mScrollLayout.SetOnViewChangeListener(this);
    }

    private OnClickListener onClick = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.startBtn:
                    Intent _mainactivity = new Intent(WelcomeActivity.this,start.class);
                    startActivity(_mainactivity);
                    break;
            }
        }
    };


    public void OnViewChange(int position) {
        setcurrentPoint(position);
    }

    private void setcurrentPoint(int position) {
        if(position < 0 || position > count -1 || currentItem == position) {
            return;
        }
        imgs[currentItem].setEnabled(true);
        imgs[position].setEnabled(false);
        if(position==0)
        {
            imgs[0].setImageResource(R.drawable.page_indicator_unfocused);
            imgs[1].setImageResource(R.drawable.page_indicator_focused);
            imgs[2].setImageResource(R.drawable.page_indicator_focused);
            imgs[3].setImageResource(R.drawable.page_indicator_focused);
        }
        if(position==1)
        {
            imgs[0].setImageResource(R.drawable.page_indicator_focused);
            imgs[1].setImageResource(R.drawable.page_indicator_unfocused);
            imgs[2].setImageResource(R.drawable.page_indicator_focused);
            imgs[3].setImageResource(R.drawable.page_indicator_focused);
        }
        if(position==2)
        {
            imgs[0].setImageResource(R.drawable.page_indicator_focused);
            imgs[1].setImageResource(R.drawable.page_indicator_focused);
            imgs[2].setImageResource(R.drawable.page_indicator_unfocused);
            imgs[3].setImageResource(R.drawable.page_indicator_focused);
        }
        if(position==3)
        {
            imgs[0].setImageResource(R.drawable.page_indicator_focused);
            imgs[1].setImageResource(R.drawable.page_indicator_focused);
            imgs[2].setImageResource(R.drawable.page_indicator_focused);
            imgs[3].setImageResource(R.drawable.page_indicator_unfocused);
        }
        currentItem = position;
    }
}