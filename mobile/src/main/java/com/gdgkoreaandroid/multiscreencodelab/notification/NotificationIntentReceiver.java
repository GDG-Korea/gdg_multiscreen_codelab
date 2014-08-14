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

    public static final String ACTION_EXAMPLE =
            "com.gdgkoreaandroid.multiscreencodelab.ACTION_EXAMPLE";
    public static final String ACTION_ENABLE_MESSAGES =
            "com.gdgkoreaandroid.multiscreencodelab.ACTION_ENABLE_MESSAGES";
    public static final String ACTION_DISABLE_MESSAGES =
            "com.gdgkoreaandroid.multiscreencodelab.ACTION_DISABLE_MESSAGES";

    private boolean mEnableMessages = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_EXAMPLE)) {
            if (mEnableMessages) {
                String message = intent.getStringExtra(NotificationUtil.EXTRA_MESSAGE);
                Bundle remoteInputResults = RemoteInput.getResultsFromIntent(intent);
                CharSequence replyMessage = null;
                if (remoteInputResults != null) {
                    replyMessage = remoteInputResults.getCharSequence(NotificationUtil.EXTRA_REPLY);
                }
                if (replyMessage != null) {
                    message = message + ": \"" + replyMessage + "\"";
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }else if (intent.getAction().equals(ACTION_ENABLE_MESSAGES)) {
            mEnableMessages = true;
        } else if (intent.getAction().equals(ACTION_DISABLE_MESSAGES)) {
            mEnableMessages = false;
        }
    }
}
