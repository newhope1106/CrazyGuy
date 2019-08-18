package com.charles.crazyguy.activities;

import android.os.Bundle;

import com.charles.crazyguy.R;
import com.charles.crazyguy.widget.BeautyCircleTouchView;

public class SurfaceActivity extends BaseActivity{
    private BeautyCircleTouchView mTouchView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view);
        mTouchView = findViewById(R.id.touch_view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTouchView.stopDraw();
    }
}
