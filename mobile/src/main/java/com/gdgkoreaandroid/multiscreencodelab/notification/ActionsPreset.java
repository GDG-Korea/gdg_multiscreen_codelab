package com.gdgkoreaandroid.multiscreencodelab.notification;

/**
 * Created by FlaShilver on 2014. 8. 10..
 */
import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Base class for notification actions presets.
 */
public abstract class ActionsPreset extends NamedPreset {
    public ActionsPreset(int nameResId) {
        super(nameResId);
    }

    /** Apply the priority to a notification builder */
    public abstract void apply(Context context, NotificationCompat.Builder builder,
                               NotificationCompat.WearableExtender wearableOptions);
}
