package com.gdgkoreaandroid.multiscreencodelab;

import android.app.Application;

import com.gdgkoreaandroid.multiscreencodelab.cast.CastManager;
import com.gdgkoreaandroid.multiscreencodelab.util.ImageDownloader;
import com.google.android.gms.cast.CastMediaControlIntent;

public class MyApplication extends Application {

    private static CastManager sCastManager;

    private static ImageDownloader sImageDownloader;

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize image downloader.
        sImageDownloader = new ImageDownloader();

        sCastManager = CastManager.getInstance(getApplicationContext());
    }

    public static ImageDownloader getImageDownloaderInstance(){
        return sImageDownloader;
    }

    public static CastManager getCastManager() {
        return sCastManager;
    }
}