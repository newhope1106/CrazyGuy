package com.charles.crazyguy.util;

import android.content.Context;

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
        }
    }

    public static void doIntent() {

    }
}
