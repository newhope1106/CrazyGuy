package com.charles.crazyguy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.charles.crazyguy.util.LogUtil;

/**
 * @author newhope1106
 * date 2019/6/12
 * 基础Activity
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(LogUtil.DEBUG) {
            LogUtil.d(this.getClass().getSimpleName(), "[onCreate] <== ");
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(LogUtil.DEBUG) {
            LogUtil.d(this.getClass().getSimpleName(), "[onNewIntent] <== ");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(LogUtil.DEBUG) {
            LogUtil.d(this.getClass().getSimpleName(), "[onStart] <== ");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(LogUtil.DEBUG) {
            LogUtil.d(this.getClass().getSimpleName(), "[onResume] <== ");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(LogUtil.DEBUG) {
            LogUtil.d(this.getClass().getSimpleName(), "[onPause] <== ");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(LogUtil.DEBUG) {
            LogUtil.d(this.getClass().getSimpleName(), "[onStop] <== ");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(LogUtil.DEBUG) {
            LogUtil.d(this.getClass().getSimpleName(), "[onDestroy] <== ");
        }
    }
}
