/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gdgkoreaandroid.multiscreencodelab.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationUtil {

    public static final String SHOW_TOAST = "com.gdgkoreaandroid.multiscreencodelab.SHOW_TOAST";
    public static final String DELETE_NOTIFI = "com.gdgkoreaandroid.multiscreencodelab.DELETE_NOTIFI";
    public static final String PLAY_NEXT = "com.gdgkoreaandroid.multiscreencodelab.PLAY_NEXT";
    public static final String PLAY_PREVIOUS = "com.gdgkoreaandroid.multiscreencodelab.PLAY_PREVIOUS";
    public static final String PLAY = "com.gdgkoreaandroid.multiscreencodelab.PLAY";
    public static final String STOP = "com.gdgkoreaandroid.multiscreencodelab.STOP";

    public static PendingIntent getToastPendingIntent(Context context, int messageResId) {
        Intent intent = new Intent(NotificationIntentReceiver.SHOW_TOAST)
                .setClass(context, NotificationIntentReceiver.class);
        return PendingIntent.getBroadcast(context, messageResId /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getNotificationDeletePendingIntent(Context context, int messageResId) {
        Intent intent = new Intent(NotificationIntentReceiver.DELETE_NOTIFI)
                .setClass(context, NotificationIntentReceiver.class);
        return PendingIntent.getBroadcast(context, messageResId /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getPlayPendingIntent(Context context, int messageResId) {
        Intent intent = new Intent(NotificationIntentReceiver.PLAY)
                .setClass(context, NotificationIntentReceiver.class);
        return PendingIntent.getBroadcast(context, messageResId /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getNextPendingIntent(Context context, int messageResId) {
        Intent intent = new Intent(NotificationIntentReceiver.PLAY_NEXT)
                .setClass(context, NotificationIntentReceiver.class);
        return PendingIntent.getBroadcast(context, messageResId /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
    public static PendingIntent getPreviousPendingIntent(Context context, int messageResId) {
        Intent intent = new Intent(NotificationIntentReceiver.PLAY_PREVIOUS)
                .setClass(context, NotificationIntentReceiver.class);
        return PendingIntent.getBroadcast(context, messageResId /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getStopPendingIntent(Context context, int messageResId) {
        Intent intent = new Intent(NotificationIntentReceiver.STOP)
                .setClass(context, NotificationIntentReceiver.class);
        return PendingIntent.getBroadcast(context, messageResId /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
