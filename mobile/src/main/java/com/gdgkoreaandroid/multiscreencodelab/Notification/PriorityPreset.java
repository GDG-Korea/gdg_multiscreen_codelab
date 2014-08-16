package com.gdgkoreaandroid.multiscreencodelab.notification;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;

import com.gdgkoreaandroid.multiscreencodelab.R;

/**
 * Base class for notification priority presets.
 */
public class PriorityPreset extends NamedPreset {
    private final int mPriority;

    public static final PriorityPreset DEFAULT = new PriorityPreset(R.string.default_priority, Notification.PRIORITY_MAX) {
    };
 

    public PriorityPreset(int nameResId, int priority) {
        super(nameResId);
        mPriority = priority;
    }

    /** Apply the priority to a notification builder */
    public void apply(NotificationCompat.Builder builder,
                      NotificationCompat.WearableExtender wearableOptions){
        builder.setPriority(mPriority);
    }
}