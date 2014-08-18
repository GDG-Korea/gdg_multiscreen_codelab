package com.gdgkoreaandroid.multiscreencodelab;

import android.app.ActionBar;
import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.gdgkoreaandroid.multiscreencodelab.cast.CastListener;
import com.gdgkoreaandroid.multiscreencodelab.cast.MediaListener;
import com.gdgkoreaandroid.multiscreencodelab.data.Movie;
import com.gdgkoreaandroid.multiscreencodelab.data.MovieList;
import com.gdgkoreaandroid.multiscreencodelab.notification.NotificationUtil;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.images.WebImage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerActivity extends ActionBarActivity implements CastListener, MediaListener {

    private static final String TAG = "PlayerActivity";

    private static final int HIDE_CONTROLLER_TIME = 5000;
    private static final int SEEKBAR_DELAY_TIME = 100;
    private static final int SEEKBAR_INTERVAL_TIME = 1000;
    private static final int MIN_SCRUB_TIME = 3000;
    private static final int SCRUB_SEGMENT_DIVISOR = 30;
    private static final double MEDIA_BAR_TOP_MARGIN = 0.8;
    private static final double MEDIA_BAR_RIGHT_MARGIN = 0.2;
    private static final double MEDIA_BAR_BOTTOM_MARGIN = 0.0;
    private static final double MEDIA_BAR_LEFT_MARGIN = 0.2;
    private static final double MEDIA_BAR_HEIGHT = 0.1;
    private static final double MEDIA_BAR_WIDTH = 0.9;

    private VideoView mVideoView;
    private TextView mStartText;
    private TextView mEndText;
    private SeekBar mSeekbar;
    private ImageView mPlayPause;
    private ProgressBar mLoading;
    private View mControllers;
    private View mContainer;
    private Timer mSeekbarTimer;
    private Timer mControllersTimer;
    private PlaybackState mPlaybackState;
    private final Handler mHandler = new Handler();
    private Movie mSelectedMovie;
    private boolean mShouldStartPlayback;
    private boolean mControlersVisible;
    private int mDuration;
    private Bitmap mDefaultNotificationIcon;

    private int lastSeekPosition = 0;

    /*
     * List of various states that we can be in
     */
    public static enum PlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33000000")));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#55000000")));
        actionBar.setDisplayShowHomeEnabled(false);

        loadViews();
        setupController();
        setupControlsCallbacks();
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.getCastManager().registerCastListener(this);
        MyApplication.getCastManager().registerMediaListener(this);
        MyApplication.getCastManager().startDiscovery();

        startVideoPlayer();
        postWearNotification(mSelectedMovie);

        updateMetadata(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (null != mSeekbarTimer) {
            mSeekbarTimer.cancel();
            mSeekbarTimer = null;
        }
        if (null != mControllersTimer) {
            mControllersTimer.cancel();
        }
        mVideoView.pause();
        mPlaybackState = PlaybackState.PAUSED;
        updatePlayButton(PlaybackState.PAUSED);

        MyApplication.getCastManager().stopDiscovery();
        MyApplication.getCastManager().unregisterMediaListener();
        MyApplication.getCastManager().unregisterCastListener();
    }

    @Override
    protected void onDestroy() {
        stopControllersTimer();
        stopSeekBarTimer();

        NotificationManagerCompat.from(this).cancel(NotificationUtil.WEAR_NOTIFICAITON_ID);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_player, menu);

        // Prepare MediaRoute Action Provider
        MenuItem castMenu = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(castMenu);
        mediaRouteActionProvider.setRouteSelector(
                MyApplication.getCastManager().getMediaRouteSelector());

        return super.onCreateOptionsMenu(menu);
    }

    private void startVideoPlayer() {
        Bundle b = getIntent().getExtras();

        long movieId = getIntent().getLongExtra(MovieList.ARG_ITEM_ID, MovieList.INVALID_ID);
        boolean play = getIntent().getBooleanExtra("play", false);
        boolean pause = getIntent().getBooleanExtra("pause", false);

        if(play){
            mPlaybackState = PlaybackState.PLAYING;
            updatePlayButton(mPlaybackState);

            if(MyApplication.getCastManager().isApplicationStarted()) {
                MyApplication.getCastManager().playMedia();
            }else {
                mVideoView.start();
            }
            startControllersTimer();
        }else if(pause) {
            if(MyApplication.getCastManager().isApplicationStarted()) {
                MyApplication.getCastManager().pauseMedia();
            }else {
                mVideoView.pause();
            }
            mPlaybackState = PlaybackState.PAUSED;
            updatePlayButton(PlaybackState.PAUSED);
            stopControllersTimer();
        }

        if(movieId != MovieList.INVALID_ID) {
            mSelectedMovie = MovieList.getMovie(movieId);

            if (mSelectedMovie != null) {
                setTitle(mSelectedMovie.getTitle());
                mShouldStartPlayback = b.getBoolean(getResources().getString(R.string.should_start));
                int startPosition = b.getInt(getResources().getString(R.string.start_position), 0);
                mVideoView.setVideoPath(mSelectedMovie.getVideoUrl());
                if (mShouldStartPlayback) {
                    mPlaybackState = PlaybackState.PLAYING;
                    updatePlayButton(mPlaybackState);
                    if (startPosition > 0) {
                        mVideoView.seekTo(startPosition);
                    }
                    mVideoView.start();
                    mPlayPause.requestFocus();
                    startControllersTimer();
                } else {
                    updatePlaybackLocation();
                    mPlaybackState = PlaybackState.PAUSED;
                    updatePlayButton(mPlaybackState);
                }
            }
        }
    }

    private void updatePlaybackLocation() {
        if (mPlaybackState == PlaybackState.PLAYING ||
                mPlaybackState == PlaybackState.BUFFERING) {
            startControllersTimer();
        } else {
            stopControllersTimer();
        }
    }

    private void play(int position) {
        startControllersTimer();
        mVideoView.seekTo(position);
        mVideoView.start();
        restartSeekBarTimer();
    }

    private void stopSeekBarTimer() {
        Log.d(TAG, "Stopped TrickPlay Timer");
        if (null != mSeekbarTimer) {
            mSeekbarTimer.cancel();
        }
    }

    private void restartSeekBarTimer() {
        stopSeekBarTimer();
        mSeekbarTimer = new Timer();
        mSeekbarTimer.scheduleAtFixedRate(new UpdateSeekbarTask(), SEEKBAR_DELAY_TIME,
                SEEKBAR_INTERVAL_TIME);
    }

    private void stopControllersTimer() {
        if (null != mControllersTimer) {
            mControllersTimer.cancel();
        }
    }

    private void startControllersTimer() {
        if (null != mControllersTimer) {
            mControllersTimer.cancel();
        }
        mControllersTimer = new Timer();
        mControllersTimer.schedule(new HideControllersTask(), HIDE_CONTROLLER_TIME);
    }

    private void updateControllersVisibility(boolean show) {
        if (show) {
            // Show/Hide Controller UI only if using local player
            if(!MyApplication.getCastManager().isApplicationStarted()) {
                mControllers.setVisibility(View.VISIBLE);
                getSupportActionBar().show();
            }
        } else {
            if(!MyApplication.getCastManager().isApplicationStarted()) {
                mControllers.setVisibility(View.INVISIBLE);
                getSupportActionBar().hide();
            }
        }
    }

    private class HideControllersTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateControllersVisibility(false);
                    mControlersVisible = false;
                }
            });

        }
    }

    private class UpdateSeekbarTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    int currentPos = 0;

                    if (MyApplication.getCastManager().isApplicationStarted()) {
                        currentPos = (int) MyApplication.getCastManager().getMediaPlayer().getApproximateStreamPosition();
                    }else {
                        currentPos = mVideoView.getCurrentPosition();
                    }
                    updateSeekbar(currentPos, mDuration);
                }
            });
        }
    }

    private class BackToDetailTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(PlayerActivity.this, MovieDetailActivity.class);
                    intent.putExtra(MovieList.ARG_ITEM_ID, mSelectedMovie.getId());
                    startActivity(intent);
                }
            });

        }
    }

    private void setupController() {

        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int w = (int) (metrics.widthPixels * MEDIA_BAR_WIDTH);
        int h = (int) (metrics.heightPixels * MEDIA_BAR_HEIGHT);
        int marginLeft = (int) (metrics.widthPixels * MEDIA_BAR_LEFT_MARGIN);
        int marginTop = (int) (metrics.heightPixels * MEDIA_BAR_TOP_MARGIN);
        int marginRight = (int) (metrics.widthPixels * MEDIA_BAR_RIGHT_MARGIN);
        int marginBottom = (int) (metrics.heightPixels * MEDIA_BAR_BOTTOM_MARGIN);
        LayoutParams lp = new LayoutParams(w, h);
        lp.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        mControllers.setLayoutParams(lp);
        mStartText.setText(getResources().getString(R.string.init_text));
        mEndText.setText(getResources().getString(R.string.init_text));
    }

    private void setupControlsCallbacks() {

        mVideoView.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                String msg = "";
                if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                    msg = getString(R.string.video_error_media_load_timeout);
                } else if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    msg = getString(R.string.video_error_server_unaccessible);
                } else {
                    msg = getString(R.string.video_error_unknown_error);
                }
                mVideoView.stopPlayback();
                mPlaybackState = PlaybackState.IDLE;
                return false;
            }
        });

        mVideoView.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "onPrepared is reached");
                mDuration = mp.getDuration();
                mEndText.setText(formatMillis(mDuration));
                mSeekbar.setMax(mDuration);
                restartSeekBarTimer();
            }
        });

        mVideoView.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                stopSeekBarTimer();
                mPlaybackState = PlaybackState.IDLE;
                updatePlayButton(PlaybackState.IDLE);
                mControllersTimer = new Timer();
                mControllersTimer.schedule(new BackToDetailTask(), HIDE_CONTROLLER_TIME);
            }
        });
    }

    /*
     * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { return
     * super.onKeyDown(keyCode, event); }
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int currentPos = 0;
        int delta = (int) (mDuration / SCRUB_SEGMENT_DIVISOR);
        if (delta < MIN_SCRUB_TIME)
            delta = MIN_SCRUB_TIME;

        Log.v("keycode", "duration " + mDuration + " delta:" + delta);
        if (!mControlersVisible) {
            updateControllersVisibility(true);
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                currentPos = mVideoView.getCurrentPosition();
                currentPos -= delta;
                if (currentPos > 0)
                    play(currentPos);
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                currentPos = mVideoView.getCurrentPosition();
                currentPos += delta;
                if (currentPos < mDuration)
                    play(currentPos);
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void updateSeekbar(int position, int duration) {
        mSeekbar.setProgress(position);
        mSeekbar.setMax(duration);
        mStartText.setText(formatMillis(position));
        mEndText.setText(formatMillis(duration));
    }

    private void updatePlayButton(PlaybackState state) {
        switch (state) {
            case PLAYING:
                mLoading.setVisibility(View.INVISIBLE);
                mPlayPause.setVisibility(View.VISIBLE);
                mPlayPause.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_pause_playcontrol_normal));
                break;
            case PAUSED:
            case IDLE:
                mLoading.setVisibility(View.INVISIBLE);
                mPlayPause.setVisibility(View.VISIBLE);
                mPlayPause.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_play_playcontrol_normal));
                break;
            case BUFFERING:
                mPlayPause.setVisibility(View.INVISIBLE);
                mLoading.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void updateMetadata(boolean visible) {
        mVideoView.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    /**
     * Formats time in milliseconds to hh:mm:ss string format.
     *
     * @param millis
     * @return
     */
    private String formatMillis(int millis) {
        String result = "";
        int hr = millis / 3600000;
        millis %= 3600000;
        int min = millis / 60000;
        millis %= 60000;
        int sec = millis / 1000;
        if (hr > 0) {
            result += hr + ":";
        }
        if (min >= 0) {
            if (min > 9) {
                result += min + ":";
            } else {
                result += "0" + min + ":";
            }
        }
        if (sec > 9) {
            result += sec;
        } else {
            result += "0" + sec;
        }
        return result;
    }


    private void loadViews() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mStartText = (TextView) findViewById(R.id.startText);
        mEndText = (TextView) findViewById(R.id.endText);
        mSeekbar = (SeekBar) findViewById(R.id.seekBar);
        mPlayPause = (ImageView) findViewById(R.id.playpause);
        mLoading = (ProgressBar) findViewById(R.id.progressBar);
        mControllers = findViewById(R.id.controllers);
        mContainer = findViewById(R.id.container);

        mContainer.setOnClickListener(mPlayPauseHandler);

        try {
            InputStream is = getAssets().open("tv_users.jpg");
            mDefaultNotificationIcon = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            //do nothing
            mDefaultNotificationIcon
                    = BitmapFactory.decodeResource(getResources(), R.drawable.example_large_icon);
        }
    }

    private final View.OnClickListener mPlayPauseHandler = new View.OnClickListener() {
        public void onClick(View v) {
            if (!mControlersVisible) {
                updateControllersVisibility(true);
            }

            if (mPlaybackState == PlaybackState.PAUSED) {
                mPlaybackState = PlaybackState.PLAYING;
                updatePlayButton(mPlaybackState);
                if(MyApplication.getCastManager().isApplicationStarted()) {
                    MyApplication.getCastManager().playMedia();
                }else {
                    mVideoView.start();
                }
                startControllersTimer();
            } else {
                if(MyApplication.getCastManager().isApplicationStarted()) {
                    MyApplication.getCastManager().pauseMedia();
                }else {
                    mVideoView.pause();
                }
                mPlaybackState = PlaybackState.PAUSED;
                updatePlayButton(PlaybackState.PAUSED);
                stopControllersTimer();
            }

        }
    };

    @Override
    public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo route) {
        // If connection succeeds, onConnected(Bundle) will be called
        MyApplication.getCastManager().connect();

        // Stop current playing session on phone
        mVideoView.pause();
        mPlaybackState = PlaybackState.PAUSED;
        updatePlayButton(PlaybackState.PAUSED);
        stopControllersTimer();

        // Mark last playing position
        lastSeekPosition = mVideoView.getCurrentPosition();
    }

    @Override
    public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo route) {
        // Switch player to local mode.
        lastSeekPosition = 0;
        mPlaybackState = PlaybackState.PAUSED;
        updatePlayButton(PlaybackState.PAUSED);
        startControllersTimer();
    }

    @Override
    public void onConnected(Bundle bundle) {
        CastDevice dev = MyApplication.getCastManager().getCurrentDevice();
        Toast.makeText(this,
                "Connected to " + dev.getFriendlyName(), Toast.LENGTH_SHORT).show();
        // If launch succeeds, onApplicationLaunched(boolean) will be called
        MyApplication.getCastManager().launchApplication();
    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onApplicationLaunched(boolean wasLaunched) {
        MyApplication.getCastManager().attachMediaPlayer();

        MediaMetadata metadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        metadata.putString(MediaMetadata.KEY_TITLE, mSelectedMovie.getTitle());
        metadata.putString(MediaMetadata.KEY_STUDIO, mSelectedMovie.getStudio());
        metadata.addImage(new WebImage(Uri.parse(mSelectedMovie.getCardImageUrl())));

        MediaInfo info = new MediaInfo.Builder(mSelectedMovie.getVideoUrl())
                .setMetadata(metadata)
                .setContentType("video/mp4")
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED).build();
        // If media loaded without error, onMediaLoaded(MediaChannelResult) will be called
        MyApplication.getCastManager().loadMedia(info);
    }

    @Override
    public void onApplicationStatusChanged() {

    }

    @Override
    public void onVolumeChanged() {

    }

    @Override
    public void onApplicationDisconnected(int statusCode) {

    }

    @Override
    public void onMediaLoaded(RemoteMediaPlayer.MediaChannelResult result) {
        // Media is now playing on the Cast device.
        mPlaybackState = PlaybackState.PLAYING;
        updatePlayButton(PlaybackState.PLAYING);

        // Resume last seek position
        MyApplication.getCastManager().seekTo(lastSeekPosition);
    }

    @Override
    public void onMediaLoadFailed(RemoteMediaPlayer.MediaChannelResult result) {

    }

    @Override
    public void onMediaControl(int controlType, RemoteMediaPlayer.MediaChannelResult result) {

    }

    @Override
    public void onMediaMetadataUpdated() {

    }

    @Override
    public void onMediaStatusUpdated(MediaStatus status) {

    }

    private void postWearNotification(Movie movie) {
        //This method should be implemented by codelab attendees

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle("Now Playing")
                .setContentText(mSelectedMovie.getTitle())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(mDefaultNotificationIcon);

        NotificationCompat.Action playpause = new NotificationCompat.Action.Builder(
                R.drawable.ic_playnstop, getString(R.string.play),
                NotificationUtil.getPlayOrPausePendingIntent(this, mPlaybackState)).build();

        //builder.addAction(previousAction).addAction(nextAction);
        NotificationCompat.WearableExtender wearableOptions =
                new NotificationCompat.WearableExtender();

        wearableOptions.setDisplayIntent(NotificationUtil.getChangeMoviePendingIntent(this, 1));

        NotificationCompat.Action previousAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_previous,
                getString(R.string.previous),
                NotificationUtil.getChangeMoviePendingIntent(this,
                        MovieList.getPreviousMovie(movie).getId())).build();

        wearableOptions.addAction(playpause);
        wearableOptions.setContentAction(0);

        NotificationCompat.Action nextAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_next, getString(R.string.next),
                NotificationUtil.getChangeMoviePendingIntent(this,
                        MovieList.getNextMovie(movie).getId())).build();
        wearableOptions.addAction(previousAction).addAction(nextAction);

        builder.extend(wearableOptions);

        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(
                NotificationUtil.WEAR_NOTIFICAITON_ID, notification);
    }
}
