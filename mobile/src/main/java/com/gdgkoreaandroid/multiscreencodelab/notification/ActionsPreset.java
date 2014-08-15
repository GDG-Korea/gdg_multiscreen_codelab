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
        NotificationCompat.Action action1 = new NotificationCompat.Action.Builder(R.drawable.ic_full_action, context.getString(R.string.example_action), NotificationUtil.getExamplePendingIntent(context,
                R.string.example_action_clicked)).build();
        NotificationCompat.Action action2 = new NotificationCompat.Action.Builder(R.drawable.ic_full_action, context.getString(R.string.example_action), NotificationUtil.getExamplePendingIntent(context,
                R.string.example_action_clicked)).build();
        wearableOptions.addAction(action1).addAction(action2);

        builder.setSmallIcon(R.drawable.ic_full_action)
               .setContentTitle("ContentTitle")
               .setContentText("abcdefg")
               .extend(wearableOptions)
               .build();

//        builder.addAction(R.drawable.ic_full_action,
//                context.getString(R.string.example_action),
//                NotificationUtil.getExamplePendingIntent(context,
//                        R.string.example_action_clicked))
//                .addAction(R.drawable.ic_full_action,
//                        context.getString(R.string.example_action),
//                        NotificationUtil.getExamplePendingIntent(context,
//                                R.string.example_action_clicked))
//                .build();
    }
}
