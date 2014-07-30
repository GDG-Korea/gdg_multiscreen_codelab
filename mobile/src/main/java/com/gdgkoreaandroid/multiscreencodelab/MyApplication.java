package com.gdgkoreaandroid.multiscreencodelab;

import android.app.Application;

public class MyApplication extends Application {

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