package com.charles.crazyguy.util;

/**
 * Created by Liu Liaopu on 2019-08-19.
 */
public class CommonUtil {
    public static String formatVideoTime(long millisecond) {
        int seconds = (int)( millisecond / 1000);
        int hour = seconds / 3600;
        int minute = (seconds%3600) / 60;
        int second = seconds % 60;
        StringBuilder sb = new StringBuilder();
        sb.append(hour < 10 ? "0" + hour : hour);
        sb.append(":");
        sb.append(minute < 10 ? "0" + minute: minute);
        sb.append(":");
        sb.append(second < 10 ? "0" + second : second);

        return sb.toString();
    }
}
