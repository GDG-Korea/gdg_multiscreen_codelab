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
public abstract class ActionsPreset extends NamedPreset {

    public static final ActionsPreset ACTION_PRESET = new SingleActionPreset();


    public ActionsPreset(int nameResId) {
        super(nameResId);
    }

    /**
     * Apply the priority to a notification builder
     */
    public abstract void apply(Context context, NotificationCompat.Builder builder,
                               NotificationCompat.WearableExtender wearableOptions);

    private static class SingleActionPreset extends ActionsPreset {
        public SingleActionPreset() {
            super(R.string.single_action);
        }

        @Override
        public void apply(Context context, NotificationCompat.Builder builder,
                NotificationCompat.WearableExtender wearableOptions) {
            builder.addAction(R.drawable.ic_full_action,
                    context.getString(R.string.example_action),
                    NotificationUtil.getExamplePendingIntent(context,
                            R.string.example_action_clicked))
                    .addAction(R.drawable.ic_full_action,
                    context.getString(R.string.example_action),
                    NotificationUtil.getExamplePendingIntent(context,
                            R.string.example_action_clicked))
                    .build();
        }
    }
}
