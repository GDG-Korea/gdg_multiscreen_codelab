package com.gdgkoreaandroid.multiscreencodelab.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationUtil {

    public static final String SHOW_TOAST = "com.gdgkoreaandroid.multiscreencodelab.SHOW_TOAST";
    public static final String DELETE_NOTIFI = "com.gdgkoreaandroid.multiscreencodelab.DELETE_NOTIFI";
    public static final String PLAY_NEXT = "com.gdgkoreaandroid.multiscreencodelab.PLAY_NEXT";
    public static final String PLAY_PREVIOUS = "com.gdgkoreaandroid.multiscreencodelab.PLAY_PREVIOUS";
    public static final String PLAY = "com.gdgkoreaandroid.multiscreencodelab.PLAY";
    public static final String STOP = "com.gdgkoreaandroid.multiscreencodelab.STOP";

    public static PendingIntent getToastPendingIntent(Context context, int messageResId) {
        Intent intent = new Intent(NotificationIntentReceiver.SHOW_TOAST)
                .setClass(context, NotificationIntentReceiver.class);
        return PendingIntent.getBroadcast(context, messageResId /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getNotificationDeletePendingIntent(Context context, int messageResId) {
        Intent intent = new Intent(NotificationIntentReceiver.DELETE_NOTIFI)
                .setClass(context, NotificationIntentReceiver.class);
        return PendingIntent.getBroadcast(context, messageResId /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getPlayPendingIntent(Context context, int messageResId) {
        Intent intent = new Intent(NotificationIntentReceiver.PLAY)
                .setClass(context, NotificationIntentReceiver.class);
        return PendingIntent.getBroadcast(context, messageResId /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getNextPendingIntent(Context context, int messageResId) {
        Intent intent = new Intent(NotificationIntentReceiver.PLAY_NEXT)
                .setClass(context, NotificationIntentReceiver.class);
        return PendingIntent.getBroadcast(context, messageResId /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
    public static PendingIntent getPreviousPendingIntent(Context context, int messageResId) {
        Intent intent = new Intent(NotificationIntentReceiver.PLAY_PREVIOUS)
                .setClass(context, NotificationIntentReceiver.class);
        return PendingIntent.getBroadcast(context, messageResId /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getStopPendingIntent(Context context, int messageResId) {
        Intent intent = new Intent(NotificationIntentReceiver.STOP)
                .setClass(context, NotificationIntentReceiver.class);
        return PendingIntent.getBroadcast(context, messageResId /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}