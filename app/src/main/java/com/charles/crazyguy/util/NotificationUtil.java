package com.charles.crazyguy.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import static android.app.Notification.VISIBILITY_SECRET;

public class NotificationUtil {
    public static void showNotification(Context context, String ticker, String contentTitle, String content, int iconResId){
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setTicker(ticker)
                .setContentTitle(contentTitle)
                .setContentText(content)
                .setSmallIcon(iconResId)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "channel name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            //是否绕过请勿打扰模式
            channel.canBypassDnd();
            //闪光灯
            channel.enableLights(true);
            //锁屏显示通知
            channel.setLockscreenVisibility(VISIBILITY_SECRET);
            //闪关灯的灯光颜色
            channel.setLightColor(Color.RED);
            //桌面launcher的消息角标
            channel.canShowBadge();
            //是否允许震动
            channel.enableVibration(true);
            //获取系统通知响铃声音的配置
            channel.getAudioAttributes();
            //获取通知取到组
            channel.getGroup();
            //设置可绕过  请勿打扰模式
            channel.setBypassDnd(true);
            //设置震动模式
            channel.setVibrationPattern(new long[]{100, 100, 200});
            //是否会有灯光
            channel.shouldShowLights();
            manager.createNotificationChannel(channel);
            builder.setChannelId("default");
        }

        manager.notify(3, builder.build());
    }
}
