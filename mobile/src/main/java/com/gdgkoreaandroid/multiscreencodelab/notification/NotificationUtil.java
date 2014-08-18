package com.gdgkoreaandroid.multiscreencodelab.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.gdgkoreaandroid.multiscreencodelab.PlayerActivity;
import com.gdgkoreaandroid.multiscreencodelab.R;
import com.gdgkoreaandroid.multiscreencodelab.data.MovieList;

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

    public static PendingIntent getPlayPendingIntent(Context context, long nextMovieId) {

        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(MovieList.ARG_ITEM_ID, nextMovieId);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        Intent intent = new Intent(NotificationIntentReceiver.PLAY)
//                .setClass(context, NotificationIntentReceiver.class);
//        return PendingIntent.getBroadcast(context, messageResId /* requestCode */, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);

    }

    public static PendingIntent getChangeMoviePendingIntent(Context context, long nextMovieId) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(MovieList.ARG_ITEM_ID, nextMovieId);
        intent.putExtra(context.getString(R.string.should_start), true);
        return PendingIntent.getActivity(context, (int) nextMovieId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getStopPendingIntent(Context context, int messageResId) {
        Intent intent = new Intent(NotificationIntentReceiver.STOP)
                .setClass(context, NotificationIntentReceiver.class);
        return PendingIntent.getBroadcast(context, messageResId /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}