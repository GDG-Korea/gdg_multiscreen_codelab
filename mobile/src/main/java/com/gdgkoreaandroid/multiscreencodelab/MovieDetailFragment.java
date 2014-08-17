package com.gdgkoreaandroid.multiscreencodelab;

import android.app.Fragment;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gdgkoreaandroid.multiscreencodelab.data.Movie;
import com.gdgkoreaandroid.multiscreencodelab.data.MovieList;
import com.gdgkoreaandroid.multiscreencodelab.notification.ActionsPreset;
import com.gdgkoreaandroid.multiscreencodelab.notification.NotificationPreset;
import com.gdgkoreaandroid.multiscreencodelab.notification.PriorityPreset;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment implements Handler.Callback {

    private static final int MSG_POST_NOTIFICATIONS = 0;
    private static final long POST_NOTIFICATIONS_DELAY_MS = 200;

    private Handler mHandler;

    private Movie mMovie;

    private int postedNotificationCount = 0;

    ImageView play;
    View darkLayer;
    ProgressBar loadProgress;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MovieList.ARG_ITEM_ID)) {
            long id = getArguments().getLong(MovieList.ARG_ITEM_ID);
            mMovie = MovieList.getMovie(id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mHandler = new Handler(this);

        if (mMovie != null) {

            final ImageView thumbnail = (ImageView) rootView.findViewById(R.id.movie_detail_thumb);

            TextView title = (TextView) rootView.findViewById(R.id.movie_detail_title);
            TextView meta = (TextView) rootView.findViewById(R.id.movie_detail_meta);
            TextView description = (TextView) rootView.findViewById(R.id.movie_detail_descritpion);
            play = (ImageView) rootView.findViewById(R.id.movie_detail_play);
            darkLayer = rootView.findViewById(R.id.movie_detail_layer);
            loadProgress = (ProgressBar) rootView.findViewById(R.id.movie_detail_progress);

            View thumbContainer = rootView.findViewById(R.id.movie_detail_thumb_container);

            thumbnail.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    thumbnail.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    if(thumbnail.isEnabled()){
                        MyApplication.getImageDownloaderInstance().downloadImage(
                                mMovie.getBackgroundImageUrl(), thumbnail);
                    }
                }
            });

            title.setText(mMovie.getTitle());
            meta.setText(mMovie.getStudio());
            description.setText(mMovie.getDescription());

            play.setOnClickListener(mOnPlayVideoHandler);
            thumbContainer.setOnClickListener(mOnPlayVideoHandler);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        postNotifications();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private final View.OnClickListener mOnPlayVideoHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), PlayerActivity.class);
            intent.putExtra(MovieList.ARG_ITEM_ID, mMovie.getId());
            intent.putExtra(v.getContext().getString(R.string.should_start), true);

            postNotifications();

            v.getContext().startActivity(intent);
        }
    };

    /**
     * Begin to re-post the sample notification(s).
     */

    private void postNotifications() {

        NotificationPreset preset = NotificationPreset.PRESETS;

        //Todo preset 제작하기.
        CharSequence titlePreset = "GDG MultipleCodeLab";
        CharSequence textPreset = "This is hellCodeLab";
        PriorityPreset priorityPreset = PriorityPreset.DEFAULT;

        ActionsPreset actionsPreset = ActionsPreset.ACTION_PRESET; //Todo : 어떤 Action을 제공할건지 여기서 결정해야함.

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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

}
