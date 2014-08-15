package com.gdgkoreaandroid.multiscreencodelab.notification;

/**
 * Created by FlaShilver on 2014. 8. 10..
 */

import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.gdgkoreaandroid.multiscreencodelab.R;

/**
 * Base class for notification actions presets.
 */
public class ActionsPreset extends NamedPreset {

    public static final ActionsPreset ACTION_PRESET = new ActionsPreset();


    public ActionsPreset() {
        super(R.string.single_action);
    }

    /**
     * Apply the priority to a notification builder
     */
    public void apply(Context context, NotificationCompat.Builder builder,
                      NotificationCompat.WearableExtender wearableOptions) {

        NotificationCompat.Action playnstop = new NotificationCompat.Action.Builder(R.drawable.ic_full_action, context.getString(R.string.example_action), NotificationUtil.getToastPendingIntent(context,
                R.string.example_action_clicked)).build();
        NotificationCompat.Action previous = new NotificationCompat.Action.Builder(R.drawable.ic_full_action, context.getString(R.string.example_action), NotificationUtil.getToastPendingIntent(context,
                R.string.example_action_clicked)).build();
        NotificationCompat.Action next = new NotificationCompat.Action.Builder(R.drawable.ic_full_action, context.getString(R.string.example_action), NotificationUtil.getToastPendingIntent(context,
                R.string.example_action_clicked)).build();
        wearableOptions.addAction(playnstop).addAction(previous).addAction(next);

        builder.extend(wearableOptions)
                .build();
    }
}
