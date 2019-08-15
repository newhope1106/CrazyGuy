package com.charles.crazyguy.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.charles.crazyguy.R;
import com.charles.crazyguy.widget.CustomLinearGradientDrawable;

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

        View contentView = findViewById(R.id.content_view);
        contentView.setBackground(new CustomLinearGradientDrawable());
    }
}
