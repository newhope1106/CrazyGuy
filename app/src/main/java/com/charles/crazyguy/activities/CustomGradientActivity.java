package com.charles.crazyguy.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.charles.crazyguy.R;
import com.charles.crazyguy.util.HalfScreenPageHelper;

/**
 * 自定义渐变背景
 * @author newhope1106
 * date 2019/8/15
 */
public class CustomGradientActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_gradient);
        View rootView = findViewById(R.id.root_view);
        rootView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                android.util.Log.e("xxxx", "click Me");
            }
        });

        View contentView = findViewById(R.id.content_view);
//        contentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                android.util.Log.e("xxxx", "click abc");
//            }
//        });
        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                android.util.Log.e("xxxx", "action : " + motionEvent.getAction());
                return true;
            }
        });
        HalfScreenPageHelper.rebuild(contentView, HalfScreenPageHelper.HORIZONTAL_FULL_SCREEN);

        View contentView2 = findViewById(R.id.content_view_2);
        contentView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                android.util.Log.e("xxxx", "action 222 : " + motionEvent.getAction());
                return true;
            }
        });
        HalfScreenPageHelper.rebuild(contentView2, HalfScreenPageHelper.VERTICAL_FULL_SCREEN);
    }
}
