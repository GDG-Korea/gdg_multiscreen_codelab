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

import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.gdgkoreaandroid.multiscreencodelab.R;

/**
 * Collection of notification actions presets.
 */
public class ActionsPresets {
    public static final ActionsPreset ACTION_PRESET = new SingleActionPreset();

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
