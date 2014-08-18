package com.gdgkoreaandroid.multiscreencodelab.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.gdgkoreaandroid.multiscreencodelab.MovieListActivity;
import com.gdgkoreaandroid.multiscreencodelab.PlayerActivity;
import com.gdgkoreaandroid.multiscreencodelab.R;
import com.gdgkoreaandroid.multiscreencodelab.data.MovieList;

public class NotificationUtil {

    public static PendingIntent getContentPendingIntent(Context context, int messageResId) {
        Intent intent = new Intent(context, MovieListActivity.class);
        return PendingIntent.getActivity(context, messageResId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getNotificationDeletePendingIntent(Context context, int messageResId) {
        Intent intent = new Intent(NotificationIntentReceiver.DELETE_NOTIFI)
                .setClass(context, NotificationIntentReceiver.class);
        return PendingIntent.getBroadcast(context, messageResId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getChangeMoviePendingIntent(Context context, long nextMovieId) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(MovieList.ARG_ITEM_ID, nextMovieId);
        intent.putExtra(context.getString(R.string.should_start), true);
        return PendingIntent.getActivity(context, (int) nextMovieId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}