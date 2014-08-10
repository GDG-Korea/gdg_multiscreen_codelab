package com.gdgkoreaandroid.multiscreencodelab.Notification;

import android.app.Notification;
import android.content.Context;

/**
 * Created by FlaShilver on 2014. 8. 10..
 */
public abstract class NotificationPreset extends NamedPreset {

    public final int titleResId;
    public final int textResId;

    public NotificationPreset(int nameResId, int titleResId, int textResId) {
        super(nameResId);
        this.titleResId = titleResId;
        this.textResId = textResId;
    }

    public static class BuildOptions {
        public final CharSequence titlePreset;
        public final CharSequence textPreset;
        public final PriorityPreset priorityPreset;
        public final ActionsPreset actionsPreset;
        public final boolean includeLargeIcon;
        public final boolean hasContentIntent;
        public final Integer[] backgroundIds;

        public BuildOptions(CharSequence titlePreset, CharSequence textPreset,
                            PriorityPreset priorityPreset, ActionsPreset actionsPreset,
                            boolean includeLargeIcon, boolean hasContentIntent,Integer[] backgroundIds) {
            this.titlePreset = titlePreset;
            this.textPreset = textPreset;
            this.priorityPreset = priorityPreset;
            this.actionsPreset = actionsPreset;
            this.includeLargeIcon = includeLargeIcon;
            this.hasContentIntent = hasContentIntent;
            this.backgroundIds = backgroundIds; }
    }

    /** Build a notification with this preset and the provided options */
    public abstract Notification[] buildNotifications(Context context, BuildOptions options);

    /** Whether actions are required to use this preset. */
    public boolean actionsRequired() {
        return false;
    }

    /** Number of background pickers required */
    public int countBackgroundPickersRequired() {
        return 0;
    }
}
