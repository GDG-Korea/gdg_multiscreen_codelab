package com.gdgkoreaandroid.multiscreencodelab.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.gdgkoreaandroid.multiscreencodelab.PlayerActivity;
import com.gdgkoreaandroid.multiscreencodelab.R;
import com.gdgkoreaandroid.multiscreencodelab.data.MovieList;

public class NotificationUtil {

    public static final int WEAR_NOTIFICAITON_ID = 1001;
    private static final int PLAY_N_PAUSE_ID = 4327;

    public static PendingIntent getChangeMoviePendingIntent(Context context, long nextMovieId) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(MovieList.ARG_ITEM_ID, nextMovieId);
        intent.putExtra(context.getString(R.string.should_start), true);
        return PendingIntent.getActivity(context, (int) nextMovieId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getPlayOrPausePendingIntent(
            PlayerActivity context, PlayerActivity.PlaybackState playstate) {

        Intent intent = new Intent(context, PlayerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        switch (playstate)
        {
            case PLAYING:
            case BUFFERING:
                intent.putExtra("pause", true);
                break;
            default:
                intent.putExtra("play", true);
                break;

        }
        return PendingIntent.getActivity(context, PLAY_N_PAUSE_ID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}