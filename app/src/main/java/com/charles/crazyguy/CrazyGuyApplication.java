package com.charles.crazyguy;

import android.app.Application;

import com.charles.crazyguy.util.NavUtil;

public class CrazyGuyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NavUtil.install(this);
    }
}
