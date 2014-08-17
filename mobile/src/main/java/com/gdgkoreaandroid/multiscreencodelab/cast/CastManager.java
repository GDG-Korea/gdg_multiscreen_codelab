package com.gdgkoreaandroid.multiscreencodelab.cast;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RemoteControlClient;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

public class CastManager implements
        RemoteMediaPlayer.OnStatusUpdatedListener,
        RemoteMediaPlayer.OnMetadataUpdatedListener{

    private static final boolean DEBUG = true;
    private static final String TAG = "CastManager";

    /**
     * Use default media receiver
     */
    private static final String APPLICATION_ID =
            CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID;

    private static final String KEY_SESSION_ID = "session_id";

    private static CastManager mInstance;
    private Context mContext;

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mRouteSelector;
    private MediaRouteCallback mRouteCallback;
    private CastDevice mCurrentDevice;

    private GoogleApiClient mApiClient;
    private ConnectionStatusListener mConnectionListener;
    private RemoteMediaPlayer mMediaPlayer;

    private CastListener mCastListener;
    private MediaListener mMediaListener;

    private RemoteControlClient mRemoteControlClient;

    private boolean isApplicationStarted = false;

    public static CastManager getInstance(Context context) {
        if(mInstance==null) {
            mInstance = new CastManager(context);
        }
        return mInstance;
    }

    public CastManager(Context context) {
        this.mContext = context;

        mMediaRouter = MediaRouter.getInstance(mContext);
        mRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(
                        CastMediaControlIntent.categoryForCast(APPLICATION_ID)).build();

        mRouteCallback = new MediaRouteCallback();
        mConnectionListener = new ConnectionStatusListener();

        setSessionId(null); // Flush session id
    }

    public void registerCastListener(CastListener listener) {
        this.mCastListener = listener;
    }

    public void unregisterCastListener() {
        this.mCastListener = null;
    }

    public void registerMediaListener(MediaListener listener) {
        this.mMediaListener = listener;
    }

    public void unregisterMediaListener() {
        this.mMediaListener = null;
    }

    public MediaRouteSelector getMediaRouteSelector() {
        return mRouteSelector;
    }

    public boolean isConnected() {
        return mApiClient!=null && mApiClient.isConnected();
    }

    public boolean isApplicationStarted() {
        return isConnected() && isApplicationStarted;
    }

    public CastDevice getCurrentDevice() {
        return mCurrentDevice;
    }

    public RemoteMediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    /**
     * Start discovering available Cast device.
     */
    public void startDiscovery() {
        Log.i(TAG, "Device discovery started");
        mMediaRouter.addCallback(mRouteSelector, mRouteCallback,
                MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
    }

    /**
     * Stop discovering Cast device.
     */
    public void stopDiscovery() {
        Log.i(TAG, "Device discovery stopped");
        mMediaRouter.removeCallback(mRouteCallback);
    }

    /**
     * Connect to Cast device.
     */
    public void connect() {
        if(mCurrentDevice==null) {
            throw new IllegalStateException("No device selected.");
        }

        mApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Cast.API,
                        Cast.CastOptions.builder(mCurrentDevice, mCastClientListener)
                                .setVerboseLoggingEnabled(true).build())
                .addConnectionCallbacks(mConnectionListener)
                .addOnConnectionFailedListener(mConnectionListener)
                .build();
        mApiClient.connect();
    }

    /**
     * Disconnect from Cast device.
     */
    public void disconnect() {
        if(mApiClient.isConnected()) {
            Cast.CastApi.stopApplication(mApiClient);
            mApiClient.disconnect();
        }
    }

    /**
     * Launch selected application.
     */
    public void launchApplication() {
        if(mApiClient==null) {
            throw new IllegalStateException("No client connected.");
        }

        Cast.CastApi
                .launchApplication(mApiClient, APPLICATION_ID, false)
                .setResultCallback(new ResultCallback<Cast.ApplicationConnectionResult>() {
                    @Override
                    public void onResult(Cast.ApplicationConnectionResult result) {
                        Status status = result.getStatus();

                        if (status.isSuccess()) {
                            isApplicationStarted = true;
                            setSessionId(result.getSessionId());

                            Log.i(TAG, "onApplicationLaunched(), wasLaunched="+result.getWasLaunched());
                            if(mCastListener!=null) {
                                mCastListener.onApplicationLaunched(result.getWasLaunched());
                            }
                        } else {
                            tearDown();
                        }
                    }
                });
    }

    private void tearDown() {
        // If connected to client
        if (mApiClient != null) {
            if (isApplicationStarted) {
                if (mMediaPlayer!=null) {
                    detachMediaPlayer();
                }
                disconnect();
                isApplicationStarted = false;
                //mPlayerStatus = MediaStatus.PLAYER_STATE_IDLE;
            }
            mApiClient = null;
        }
        mCurrentDevice = null;
    }

    public void attachMediaPlayer() {
        if(mMediaPlayer!=null) {
            return;
        }

        if(mApiClient==null || !mApiClient.isConnected()) {
            throw new IllegalStateException("No client connected.");
        }

        mMediaPlayer = new RemoteMediaPlayer();
        mMediaPlayer.setOnStatusUpdatedListener(this);
        mMediaPlayer.setOnMetadataUpdatedListener(this);

        try{
            Cast.CastApi.setMessageReceivedCallbacks(
                    mApiClient, mMediaPlayer.getNamespace(), mMediaPlayer);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void detachMediaPlayer() {
        if(mMediaPlayer!=null && mApiClient !=null) {
            try {
                Cast.CastApi.removeMessageReceivedCallbacks(
                        mApiClient, mMediaPlayer.getNamespace());
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        mMediaPlayer = null;
    }

    public void loadMedia(MediaInfo info) {
        loadMedia(info, true);
    }

    public void loadMedia(MediaInfo info, boolean playOnLoad) {
        if(mMediaPlayer==null) {
            throw new IllegalStateException("MediaPlayer is not attached");
        }

        mMediaPlayer.load(mApiClient, info, playOnLoad)
                .setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                    @Override
                    public void onResult(RemoteMediaPlayer.MediaChannelResult result) {

                        if (result.getStatus().isSuccess()) {
                            Log.i(TAG, "Media loaded");
                            if(mMediaListener!=null) {
                                mMediaListener.onMediaLoaded(result);
                            }
                        }else {
                            Log.e(TAG, "Failed to load media");
                            Log.e(TAG, "Status code: " + result.getStatus().getStatusCode());

                            if(mMediaListener!=null) {
                                mMediaListener.onMediaLoadFailed(result);
                            }
                        }
                    }
                });
    }

    public void requestMediaStatus() {
        if(mMediaPlayer==null) {
            throw new IllegalStateException("MediaPlayer is not attached");
        }
        mMediaPlayer.requestStatus(mApiClient);
    }

    public void playMedia() {
        if(mMediaPlayer==null) {
            throw new IllegalStateException("No media exists.");
        }
        if(mMediaListener==null) {
            mMediaPlayer.play(mApiClient);
        }else {
            mMediaPlayer.play(mApiClient).setResultCallback(
                    new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                        @Override
                        public void onResult(RemoteMediaPlayer.MediaChannelResult result) {
                            mMediaListener.onMediaControl(MediaListener.ControlType.PLAY, result);
                        }
                    });
        }
    }

    public void pauseMedia() {
        if(mMediaPlayer==null) {
            throw new IllegalStateException("No media exists.");
        }
        if(mMediaListener==null) {
            mMediaPlayer.pause(mApiClient);
        }else {
            mMediaPlayer.pause(mApiClient).setResultCallback(
                    new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                        @Override
                        public void onResult(RemoteMediaPlayer.MediaChannelResult result) {
                            mMediaListener.onMediaControl(MediaListener.ControlType.PAUSE, result);
                        }
                    });
        }
    }

    public void stopMedia() {
        if(mMediaPlayer==null) {
            throw new IllegalStateException("No media exists.");
        }
        if(mMediaListener==null) {
            mMediaPlayer.stop(mApiClient);
        }else {
            mMediaPlayer.stop(mApiClient).setResultCallback(
                    new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                        @Override
                        public void onResult(RemoteMediaPlayer.MediaChannelResult result) {
                            mMediaListener.onMediaControl(MediaListener.ControlType.STOP, result);
                        }
                    });
        }
    }

    public void seekTo(int position) {
        if(mMediaPlayer==null) {
            throw new IllegalStateException("No media exists.");
        }
        if(mMediaListener==null) {
            mMediaPlayer.seek(mApiClient, position);
        }else {
            mMediaPlayer.seek(mApiClient, position).setResultCallback(
                    new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                        @Override
                        public void onResult(RemoteMediaPlayer.MediaChannelResult result) {
                            mMediaListener.onMediaControl(MediaListener.ControlType.SEEK, result);
                        }
                    });
        }
    }

    /**
     * Save connected session id.
     * @param sessionId session id
     */
    public void setSessionId(String sessionId) {
        SharedPreferences.Editor pref =
                PreferenceManager.getDefaultSharedPreferences(mContext).edit();

        pref.putString(KEY_SESSION_ID, sessionId);
        pref.apply();
    }

    /**
     * Get connected session id.
     * @return session id, or null if there is no active session
     */
    public String getSessionId() {
        String sessionId =
                PreferenceManager.getDefaultSharedPreferences(mContext).getString(KEY_SESSION_ID, null);
        if(sessionId==null) {
            Log.e(TAG, "No active session.");
        }
        return sessionId;
    }

    @Override
    public void onMetadataUpdated() {
        Log.i(TAG, "onMediaMetadataUpdated()");
        if(mMediaListener!=null) {
            mMediaListener.onMediaMetadataUpdated();
        }
    }

    @Override
    public void onStatusUpdated() {
        Log.i(TAG, "onMediaStatusUpdated()");
        if(mMediaListener!=null) {
            MediaStatus status = mMediaPlayer.getMediaStatus();
            mMediaListener.onMediaStatusUpdated(status);
        }
    }

    private class MediaRouteCallback extends MediaRouter.Callback {

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo route) {
            super.onRouteSelected(router, route);
            Log.i(TAG, "onRouteSelected(), name="+route.getName());
            // Save selected device.
            mCurrentDevice = CastDevice.getFromBundle(route.getExtras());

            if(mCastListener!=null) {
                mCastListener.onRouteSelected(router, route);
            }
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo route) {
            super.onRouteUnselected(router, route);
            Log.i(TAG, "onRouteUnselected()");
            if(mCastListener!=null) {
                mCastListener.onRouteUnselected(router, route);
            }

            // User now unselected the device. Remove it from use.
            tearDown();
        }
    }

    private class ConnectionStatusListener
            implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnected(Bundle bundle) {
            Log.i(TAG, "onConnected()");
            if(mCastListener!=null) {
                mCastListener.onConnected(bundle);
            }
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.i(TAG, "onConnectionSuspended()");
            if(mCastListener!=null) {
                mCastListener.onConnectionSuspended(cause);
            }
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e(TAG, "onConnectionFailed()");
            Log.e(TAG, "Error code: "+connectionResult.getErrorCode());

            if(mCastListener!=null) {
                mCastListener.onConnectionFailed(connectionResult);
            }
        }
    }

    private Cast.Listener mCastClientListener = new Cast.Listener() {
        @Override
        public void onApplicationStatusChanged() {
            Log.i(TAG, "onApplicationStatusChanged()");
            super.onApplicationStatusChanged();
            if(mCastListener!=null) {
                mCastListener.onApplicationStatusChanged();
            }
        }

        @Override
        public void onVolumeChanged() {
            Log.i(TAG, "onVolumeChanged()");
            super.onVolumeChanged();
            if(mCastListener!=null) {
                mCastListener.onVolumeChanged();
            }
        }

        @Override
        public void onApplicationDisconnected(int statusCode) {
            Log.i(TAG, "onApplicationDisconnected()");
            super.onApplicationDisconnected(statusCode);
            if(mCastListener!=null) {
                mCastListener.onApplicationDisconnected(statusCode);
            }
        }
    };
}
