package com.gdgkoreaandroid.multiscreencodelab.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.widget.Toast;

/**
 * Created by FlaShilver on 2014. 8. 10..
 */
public class NotificationIntentReceiver extends BroadcastReceiver {

    public static final String SHOW_TOAST = "com.gdgkoreaandroid.multiscreencodelab.SHOW_TOAST";
    public static final String DELETE_NOTIFI = "com.gdgkoreaandroid.multiscreencodelab.DELETE_NOTIFI";
    public static final String PLAY_NEXT = "com.gdgkoreaandroid.multiscreencodelab.PLAY_NEXT";
    public static final String PLAY_PREVIOUS = "com.gdgkoreaandroid.multiscreencodelab.PLAY_PREVIOUS";
    public static final String PLAY = "com.gdgkoreaandroid.multiscreencodelab.PLAY";
    public static final String STOP = "com.gdgkoreaandroid.multiscreencodelab.STOP";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SHOW_TOAST)){
            Toast.makeText(context, "success!!", Toast.LENGTH_SHORT).show();
        }else if (intent.getAction().equals(DELETE_NOTIFI)){
            Toast.makeText(context, "Notification is Deleted", Toast.LENGTH_SHORT).show();
        }else if (intent.getAction().equals(PLAY_NEXT)){
            Toast.makeText(context, "Notification is Deleted", Toast.LENGTH_SHORT).show();
        }else if (intent.getAction().equals(PLAY_PREVIOUS)){
            Toast.makeText(context, "Notification is Deleted", Toast.LENGTH_SHORT).show();
        }else if (intent.getAction().equals(PLAY)){
            Toast.makeText(context, "Notification is Deleted", Toast.LENGTH_SHORT).show();
        }else if (intent.getAction().equals(STOP)){
            Toast.makeText(context, "Notification is Deleted", Toast.LENGTH_SHORT).show();
        }
    }
}