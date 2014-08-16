package com.gdgkoreaandroid.multiscreencodelab;

import android.app.Fragment;
import android.app.Notification;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gdgkoreaandroid.multiscreencodelab.data.Movie;
import com.gdgkoreaandroid.multiscreencodelab.data.MovieList;
import com.gdgkoreaandroid.multiscreencodelab.notification.ActionsPreset;
import com.gdgkoreaandroid.multiscreencodelab.notification.NotificationPreset;
import com.gdgkoreaandroid.multiscreencodelab.notification.PriorityPreset;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.images.WebImage;

import java.io.IOException;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment
        implements RemoteMediaPlayer.OnStatusUpdatedListener,
        RemoteMediaPlayer.OnMetadataUpdatedListener {

    private Movie mMovie;

    private int postedNotificationCount = 0;

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private CastDevice mSelectedDevice;
    private MediaRouteCallback mMediaRouteCallback;

    private GoogleApiClient mApiClient;
    private ConnectionStatusListener mConnectionStatusListener;
    private RemoteMediaPlayer mRemotePlayer;

    private int mPlayerStatus = MediaStatus.PLAYER_STATE_IDLE;
    private boolean mApplicationStarted = false;

    ImageView play;
    View darkLayer;
    ProgressBar loadProgress;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMediaRouter = MediaRouter.getInstance(getActivity().getApplicationContext());
        mMediaRouteCallback = new MediaRouteCallback();
        mConnectionStatusListener = new ConnectionStatusListener();

        setHasOptionsMenu(true);

        if (getArguments().containsKey(MovieList.ARG_ITEM_ID)) {
            long id = getArguments().getLong(MovieList.ARG_ITEM_ID);
            mMovie = MovieList.getMovie(id);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        postNotifications();
        if (mApiClient != null && mApiClient.isConnected()) {
            attachMediaPlayer();
            mRemotePlayer.requestStatus(mApiClient);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

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

                    if (thumbnail.isEnabled()) {
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
    public void onStart() {
        super.onStart();
        startDiscovery();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDiscovery();
        detachMediaPlayer();
    }

    private final View.OnClickListener mOnPlayVideoHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // If there is no cast device connected, launch video player.
            if (mApiClient == null || !mApiClient.isConnected()) {
                Intent intent = new Intent(v.getContext(), PlayerActivity.class);
                intent.putExtra(MovieList.ARG_ITEM_ID, mMovie.getId());
                intent.putExtra(v.getContext().getString(R.string.should_start), true);

                postNotifications();

                v.getContext().startActivity(intent);
            } else {
                // Play video on the cast device
                switch (mPlayerStatus) {
                    case MediaStatus.PLAYER_STATE_IDLE:
                        attachMediaPlayer();

                        MediaMetadata metadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
                        metadata.putString(MediaMetadata.KEY_TITLE, mMovie.getTitle());
                        metadata.putString(MediaMetadata.KEY_STUDIO, mMovie.getStudio());
                        metadata.addImage(new WebImage(Uri.parse(mMovie.getCardImageUrl())));

                        MediaInfo info = new MediaInfo.Builder(mMovie.getVideoUrl())
                                .setMetadata(metadata)
                                .setContentType("video/mp4")
                                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED).build();

                        mRemotePlayer.load(mApiClient, info, true)
                                .setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                                    @Override
                                    public void onResult(RemoteMediaPlayer.MediaChannelResult mediaChannelResult) {
                                        if (!mediaChannelResult.getStatus().isSuccess()) {
                                            Toast.makeText(getActivity().getApplicationContext(),
                                                    "Failed to play", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        break;

                    case MediaStatus.PLAYER_STATE_PAUSED:
                        mRemotePlayer.play(mApiClient);
                        break;

                    case MediaStatus.PLAYER_STATE_PLAYING:
                        mRemotePlayer.pause(mApiClient);
                        break;
                }
            }
        }
    };

    /**
     * Begin to re-post the sample notification(s).
     */

    private void postNotifications() {
        CharSequence titlePreset = "GDG MultipleCodeLab";
        CharSequence textPreset = "This is hellCodeLab";
        NotificationPreset preset = NotificationPreset.PRESETS;
        PriorityPreset priorityPreset = PriorityPreset.DEFAULT;
        ActionsPreset actionsPreset = ActionsPreset.ACTION_PRESET;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_movie_detail, menu);

        MenuItem castMenu = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(castMenu);
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
    }

    private void startDiscovery() {
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(
                        MyApplication.MEDIA_RECEIVER_APPLICATION_ID)).build();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouteCallback,
                MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
    }

    private void attachMediaPlayer() {
        if (mRemotePlayer != null) {
            return;
        }

        mRemotePlayer = new RemoteMediaPlayer();
        mRemotePlayer.setOnStatusUpdatedListener(MovieDetailFragment.this);
        mRemotePlayer.setOnMetadataUpdatedListener(this);

        try {
            Cast.CastApi.setMessageReceivedCallbacks(
                    mApiClient, mRemotePlayer.getNamespace(), mRemotePlayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void detachMediaPlayer() {
        if (mRemotePlayer != null && mApiClient != null) {
            try {
                Cast.CastApi.removeMessageReceivedCallbacks(
                        mApiClient, mRemotePlayer.getNamespace());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mRemotePlayer = null;
    }

    private void stopDiscovery() {
        mMediaRouter.removeCallback(mMediaRouteCallback);
        mMediaRouteSelector = null;
    }

    private void tearDown() {
        if (mApiClient != null) {
            if (mApplicationStarted) {
                if (mApiClient.isConnected()) {
                    Cast.CastApi.stopApplication(mApiClient);
                    mApiClient.disconnect();
                }
                mApplicationStarted = false;
                mPlayerStatus = MediaStatus.PLAYER_STATE_IDLE;
            }
            mApiClient = null;
        }
        mSelectedDevice = null;
    }

    @Override
    public void onMetadataUpdated() {

    }

    @Override
    public void onStatusUpdated() {
        MediaStatus status = mRemotePlayer.getMediaStatus();
        mPlayerStatus = status.getPlayerState();

        // Set controller visibility according to playback state.
        switch (mPlayerStatus) {
            case MediaStatus.PLAYER_STATE_PLAYING:
                play.setVisibility(View.VISIBLE);
                play.setImageResource(R.drawable.ic_pause_playcontrol_normal);
                loadProgress.setVisibility(View.GONE);
                break;

            case MediaStatus.PLAYER_STATE_PAUSED:
            case MediaStatus.PLAYER_STATE_IDLE:
                play.setVisibility(View.VISIBLE);
                play.setImageResource(R.drawable.play_button);
                loadProgress.setVisibility(View.GONE);
                break;

            case MediaStatus.PLAYER_STATE_BUFFERING:
                play.setVisibility(View.GONE);
                loadProgress.setVisibility(View.VISIBLE);
                break;
        }
    }

    private class MediaRouteCallback extends MediaRouter.Callback {

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo route) {
            super.onRouteSelected(router, route);
            mSelectedDevice = CastDevice.getFromBundle(route.getExtras());

            mApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Cast.API, Cast.CastOptions.builder(mSelectedDevice, mCastClientListener).build())
                    .addConnectionCallbacks(mConnectionStatusListener)
                    .addOnConnectionFailedListener(mConnectionStatusListener)
                    .build();
            mApiClient.connect();

            darkLayer.setBackgroundColor(Color.parseColor("#66000000"));
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo route) {
            super.onRouteUnselected(router, route);
            tearDown();

            darkLayer.setBackgroundColor(Color.parseColor("#00000000"));
        }
    }

    private Cast.Listener mCastClientListener = new Cast.Listener() {

        @Override
        public void onApplicationDisconnected(int statusCode) {
            super.onApplicationDisconnected(statusCode);

        }
    };

    private class ConnectionStatusListener
            implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnected(Bundle bundle) {
            Cast.CastApi.launchApplication(mApiClient,
                    MyApplication.MEDIA_RECEIVER_APPLICATION_ID, false)

                    .setResultCallback(new ResultCallback<Cast.ApplicationConnectionResult>() {
                        @Override
                        public void onResult(Cast.ApplicationConnectionResult applicationConnectionResult) {
                            Status status = applicationConnectionResult.getStatus();

                            if (status.isSuccess()) {
                                mApplicationStarted = true;
                            } else {
                                tearDown();
                            }
                        }
                    });

        }

        @Override
        public void onConnectionSuspended(int cause) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Toast.makeText(getActivity().getApplication(), "Failed to connect.", Toast.LENGTH_SHORT).show();
            tearDown();
        }
    }
}
