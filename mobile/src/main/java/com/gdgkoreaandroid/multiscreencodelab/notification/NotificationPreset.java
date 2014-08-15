package com.gdgkoreaandroid.multiscreencodelab.notification;

import android.app.Notification;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.gdgkoreaandroid.multiscreencodelab.R;

/**
 * Created by FlaShilver on 2014. 8. 10..
 */
public class NotificationPreset extends NamedPreset {

    public final int titleResId;
    public final int textResId;

    public static final NotificationPreset PRESETS = new NotificationPreset();

    public NotificationPreset() {
        super(R.string.basic_example);
        this.titleResId = R.string.example_content_title;
        this.textResId = R.string.example_content_text;
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
                            boolean includeLargeIcon, boolean hasContentIntent, Integer[] backgroundIds) {
            this.titlePreset = titlePreset;
            this.textPreset = textPreset;
            this.priorityPreset = priorityPreset;
            this.actionsPreset = actionsPreset;
            this.includeLargeIcon = includeLargeIcon;
            this.hasContentIntent = hasContentIntent;
            this.backgroundIds = backgroundIds;
        }
    }

    /**
     * Build a notification with this preset and the provided options
     */
    public Notification[] buildNotifications(Context context, BuildOptions options) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        NotificationCompat.WearableExtender wearableOptions =
                new NotificationCompat.WearableExtender();
        applyBasicOptions(context, builder, wearableOptions, options);
        builder.extend(wearableOptions);

        return new Notification[]{builder.build()};
    }

    /**
     * Whether actions are required to use this preset.
     */
    public boolean actionsRequired() {
        return false;
    }

    /**
     * Number of background pickers required
     */
    public int countBackgroundPickersRequired() {
        return 0;
    }

    private static NotificationCompat.Builder applyBasicOptions(Context context,
                                                                NotificationCompat.Builder builder, NotificationCompat.WearableExtender wearableOptions,
                                                                NotificationPreset.BuildOptions options) {

        builder.setContentTitle(options.titlePreset)
                .setContentText(options.textPreset)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDeleteIntent(NotificationUtil.getNotificationDeletePendingIntent(
                        context, R.string.example_notification_deleted));
        options.actionsPreset.apply(context, builder, wearableOptions);
        options.priorityPreset.apply(builder, wearableOptions);

        builder.setLargeIcon(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.example_large_icon));
        builder.setContentIntent(NotificationUtil.getToastPendingIntent(context,
                R.string.content_intent_clicked));

        return builder;
    }
}
