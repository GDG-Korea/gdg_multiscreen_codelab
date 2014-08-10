package com.gdgkoreaandroid.multiscreencodelab;

import android.app.Fragment;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdgkoreaandroid.multiscreencodelab.Notification.ActionsPreset;
import com.gdgkoreaandroid.multiscreencodelab.Notification.ActionsPresets;
import com.gdgkoreaandroid.multiscreencodelab.Notification.NotificationIntentReceiver;
import com.gdgkoreaandroid.multiscreencodelab.Notification.NotificationPreset;
import com.gdgkoreaandroid.multiscreencodelab.Notification.NotificationPresets;
import com.gdgkoreaandroid.multiscreencodelab.Notification.PriorityPreset;
import com.gdgkoreaandroid.multiscreencodelab.Notification.PriorityPresets;
import com.gdgkoreaandroid.multiscreencodelab.dummy.Movie;
import com.gdgkoreaandroid.multiscreencodelab.dummy.MovieList;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment implements Handler.Callback {

    private static final int MSG_POST_NOTIFICATIONS = 0;
    private static final long POST_NOTIFICATIONS_DELAY_MS = 200;

    /**
     * The dummy content this fragment is presenting.
     */

    private Handler mHandler;

    private Movie mMovie;

    private int postedNotificationCount = 0;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MovieList.ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            long id = getArguments().getLong(MovieList.ARG_ITEM_ID);
            mMovie = MovieList.getMovie(id);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNotifications(false /* cancelExisting */);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mHandler = new Handler(this);

        // Show the dummy content as text in a TextView.
        if (mMovie != null) {

            ImageView thumbnail = (ImageView) rootView.findViewById(R.id.movie_detail_thumb);
            TextView title = (TextView) rootView.findViewById(R.id.movie_detail_title);
            TextView meta = (TextView) rootView.findViewById(R.id.movie_detail_meta);
            TextView description = (TextView) rootView.findViewById(R.id.movie_detail_descritpion);
            ImageView play = (ImageView) rootView.findViewById(R.id.movie_detail_play);
            View thumbContainer = rootView.findViewById(R.id.movie_detail_thumb_container);

            MyApplication.getImageDownloaderInstance().downloadImage(
                    mMovie.getBackgroundImageUrl(), thumbnail);

            title.setText(mMovie.getTitle());
            meta.setText(mMovie.getStudio());
            description.setText(mMovie.getDescription());

            play.setOnClickListener(mOnPlayVideoHandler);
            thumbContainer.setOnClickListener(mOnPlayVideoHandler);
        }

        return rootView;
    }

    private final View.OnClickListener mOnPlayVideoHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Play a video
            Intent intent = new Intent(v.getContext(), PlayerActivity.class);
            intent.putExtra(MovieList.ARG_ITEM_ID, mMovie.getId());
            intent.putExtra(v.getContext().getString(R.string.should_start), true);
            //Todo : 영상이 시작될때 Wear의 Notification Pager를 뜨게해야함. Movie의 이름과 함께. 그것은 Wear의 첫페이지에 위치시키기.
            updateNotifications(false);

            v.getContext().startActivity(intent);
        }
    };

    /**
     * Begin to re-post the sample notification(s).
     */
    private void updateNotifications(boolean cancelExisting) {
        // Disable messages to skip notification deleted messages during cancel.
        getActivity().sendBroadcast(new Intent(NotificationIntentReceiver.ACTION_DISABLE_MESSAGES)
                .setClass(getActivity(), NotificationIntentReceiver.class));

        if (cancelExisting) { // 일단 항상 false이게 해놓음
            // Cancel all existing notifications to trigger fresh-posting behavior: For example,
            // switching from HIGH to LOW priority does not cause a reordering in Notification Shade.
            NotificationManagerCompat.from(getActivity()).cancelAll();
            postedNotificationCount = 0;

            // Post the updated notifications on a delay to avoid a cancel+post race condition
            // with notification manager.
            mHandler.removeMessages(MSG_POST_NOTIFICATIONS);
            mHandler.sendEmptyMessageDelayed(MSG_POST_NOTIFICATIONS, POST_NOTIFICATIONS_DELAY_MS);
        } else {
            postNotifications(); // 항상 동영상을 재생하면 이쪽으로 오게 되는걸로.
        }
    }

    private void postNotifications() {
        getActivity().sendBroadcast(new Intent(NotificationIntentReceiver.ACTION_ENABLE_MESSAGES).setClass(getActivity(), NotificationIntentReceiver.class));

        NotificationPreset preset = NotificationPresets.PRESETS[0];

        //Todo preset 제작하기.
        CharSequence titlePreset = "GDG MultipleCodeLab";
        CharSequence textPreset = "This is hellCodeLab";
        PriorityPreset priorityPreset = PriorityPresets.DEFAULT;

        ActionsPreset actionsPreset = ActionsPresets.ACTION_PRESET; //Todo : 어떤 Action을 제공할건지 여기서 결정해야함.

        NotificationPreset.BuildOptions options = new NotificationPreset.BuildOptions(
                titlePreset,
                textPreset,
                priorityPreset,
                actionsPreset,
                true,
                true,
                null);


        Notification[] notifications = preset.buildNotifications(getActivity(), options);

        // Post new notifications
        for (int i = 0; i < notifications.length; i++) {
            NotificationManagerCompat.from(getActivity()).notify(i, notifications[i]);
        }
        // Cancel any that are beyond the current count.
        for (int i = notifications.length; i < postedNotificationCount; i++) {
            NotificationManagerCompat.from(getActivity()).cancel(i);
        }
        postedNotificationCount = notifications.length;
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case MSG_POST_NOTIFICATIONS:
                postNotifications();
                return true;
        }
        return false;
    }
}
