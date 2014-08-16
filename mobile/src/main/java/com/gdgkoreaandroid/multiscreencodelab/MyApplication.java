package com.gdgkoreaandroid.multiscreencodelab;

import android.app.Application;

import com.gdgkoreaandroid.multiscreencodelab.util.ImageDownloader;
import com.google.android.gms.cast.CastMediaControlIntent;

public class MyApplication extends Application {

    public static String MEDIA_RECEIVER_APPLICATION_ID =
            CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID;

    private static ImageDownloader sImageDownloader;

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize image downloader.
        sImageDownloader = new ImageDownloader();
    }

    public static ImageDownloader getImageDownloaderInstance(){
        return sImageDownloader;
    }
}