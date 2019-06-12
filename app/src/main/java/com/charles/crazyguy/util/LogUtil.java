package com.charles.crazyguy.util;

import android.util.Log;

/**
 * @author newhope1106
 * date 2019/6/12
 */
public class LogUtil {
    public static final boolean DEBUG = true;

    public static void i(String tag, String message) {
        Log.i(tag, message);
    }

    public static void d(String tag, String message) {
        Log.d(tag, message);
    }

    public static void e(String tag, String message) {
        Log.e(tag, message);
    }
}
