package com.gdgkoreaandroid.multiscreencodelab.notification;

import android.app.PendingIntent;
import android.content.Context;

import com.gdgkoreaandroid.multiscreencodelab.PlayerActivity;

public class NotificationUtil {

    public static final int WEAR_NOTIFICAITON_ID = 1001;
    private static final int PLAY_N_PAUSE_ID = 4327;

    public static PendingIntent getChangeMoviePendingIntent(Context context, long nextMovieId) {
        return null;
    }

    public static PendingIntent getPlayOrPausePendingIntent(
            PlayerActivity context, PlayerActivity.PlaybackState playstate) {

        return null;
    }
}