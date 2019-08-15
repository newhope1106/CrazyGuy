package com.charles.crazyguy.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.charles.crazyguy.common.Action;

import com.charles.crazyguy.R;

public class NavUtil {
    private static final String TAG = "NavUtil";

    private static Context sContext;

    public static void install(Context context){
        sContext = context;
    }

    public static void doAction(String action){
        if(LogUtil.DEBUG) {
            LogUtil.d(TAG, "[doAction] action <= " + action);
        }
        switch (action){
            case Action.ACTION_VIEW_NOTIFICATION:{
                NotificationUtil.showNotification(sContext, "News is coming!",
                        "Say Hi!", "May god bless you!", R.drawable.round_crazy_boy_icon);
                break;
            }
            case Action.ACTION_VIEW_CANVAS:{
                Intent intent = new Intent("com.charles.action.canvas");
                startActivity(intent);
                break;
            }
            case Action.ACTION_VIEW_SURFACE:{
                Intent intent = new Intent("com.charles.action.surface");
                startActivity(intent);
                break;
            }
            case Action.ACTION_VIEW_CUSTOM_GRADIENT: {
                Intent intent = new Intent("com.charles.action.custom.gradient");
                startActivity(intent);
                break;
            }
        }
    }

    public static void startActivity(Intent intent) {
        if(!(sContext instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        sContext.startActivity(intent);
    }

    public static void doIntent(Intent intent) {

    }
}
