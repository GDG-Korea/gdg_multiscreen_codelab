package com.gdgkoreaandroid.multiscreencodelab.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.gdgkoreaandroid.multiscreencodelab.MovieListActivity;

/**
 * Created by FlaShilver on 2014. 8. 10..
 */
public class NotificationIntentReceiver extends BroadcastReceiver {

    public static final String DELETE_NOTIFI = "com.gdgkoreaandroid.multiscreencodelab.DELETE_NOTIFI";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DELETE_NOTIFI)){
            Toast.makeText(context, "Notification is Deleted", Toast.LENGTH_SHORT).show();
        }
    }
}