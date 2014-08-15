package com.gdgkoreaandroid.multiscreencodelab.notification;

import android.app.Notification;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.gdgkoreaandroid.multiscreencodelab.R;

/**
 * Created by FlaShilver on 2014. 8. 10..
 */
public class NotificationPresets {
//    private static final String EXAMPLE_GROUP_KEY = "example";
//
//    public static final NotificationPreset BASIC = new BasicNotificationPreset();
//
//    public static final NotificationPreset[] PRESETS = new NotificationPreset[]{
//            BASIC
//    };
//
//    private static NotificationCompat.Builder applyBasicOptions(Context context,
//                                                                NotificationCompat.Builder builder, NotificationCompat.WearableExtender wearableOptions,
//                                                                NotificationPreset.BuildOptions options) {
//        builder.setContentTitle(options.titlePreset)
//                .setContentText(options.textPreset)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setDeleteIntent(NotificationUtil.getExamplePendingIntent(
//                        context, R.string.example_notification_deleted));
//        options.actionsPreset.apply(context, builder, wearableOptions);
//        options.priorityPreset.apply(builder, wearableOptions);
//
//        builder.setLargeIcon(BitmapFactory.decodeResource(
//                context.getResources(), R.drawable.example_large_icon));
//        builder.setContentIntent(NotificationUtil.getExamplePendingIntent(context,
//                R.string.content_intent_clicked));
//
//        return builder;
//    }
//
//    private static class BasicNotificationPreset extends NotificationPreset {
//        public BasicNotificationPreset() {
//            super(R.string.basic_example, R.string.example_content_title,
//                    R.string.example_content_text);
//        }
//
//        @Override
//        public Notification[] buildNotifications(Context context, BuildOptions options) {
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//            NotificationCompat.WearableExtender wearableOptions =
//                    new NotificationCompat.WearableExtender();
//            applyBasicOptions(context, builder, wearableOptions, options);
//            builder.extend(wearableOptions);
//            return new Notification[]{builder.build()};
//        }
//    }
}
