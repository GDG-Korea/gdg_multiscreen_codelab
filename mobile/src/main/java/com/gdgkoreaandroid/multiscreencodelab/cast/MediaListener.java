package com.gdgkoreaandroid.multiscreencodelab.cast;

import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.RemoteMediaPlayer;

public interface MediaListener {

    public class ControlType {
        public static final int PLAY = 10;
        public static final int PAUSE = 11;
        public static final int STOP = 12;
        public static final int SEEK = 13;
    }

    public void onMediaLoaded(RemoteMediaPlayer.MediaChannelResult result);
    public void onMediaLoadFailed(RemoteMediaPlayer.MediaChannelResult result);
    public void onMediaControl(int controlType, RemoteMediaPlayer.MediaChannelResult result);

    public void onMediaMetadataUpdated();
    public void onMediaStatusUpdated(MediaStatus status);
}
