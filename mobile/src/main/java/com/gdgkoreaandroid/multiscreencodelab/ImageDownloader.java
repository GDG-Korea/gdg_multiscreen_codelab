package com.gdgkoreaandroid.multiscreencodelab;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ImageDownloader {

    private Handler mMainUIHandler;
    private ExecutorService mWorkerThreadPool;
    private LruCache<String, Bitmap> mMemoryCache;

    ImageDownloader() {
        mMainUIHandler = new Handler(Looper.getMainLooper());
        mWorkerThreadPool = Executors.newFixedThreadPool(3);
        mMemoryCache = new LruCache<String, Bitmap>(30);
    }

    public void downloadImage(final String urlStr, final ImageView view){

        final URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            setErrorBitmap(view);
            return;
        }

        DownloadJob oldJob = (DownloadJob) view.getTag(R.id.download_job);

        //check current job.
        if( oldJob != null ){
            if(oldJob.mDownloadURL.equals(urlStr))
                return;

            oldJob.cancel();
        }

        //check memory cache.
        Bitmap cachedBitmap = mMemoryCache.get(urlStr);
        if( cachedBitmap != null ){
            setBitmap(view, cachedBitmap);
            return;
        }

        setEmptyBitmap(view);

        final DownloadJob newJob = new DownloadJob(urlStr);
        view.setTag(R.id.download_job, newJob);

        mWorkerThreadPool.execute(new Runnable(){
            @Override
            public void run() {

                try {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                    conn.disconnect();

                    if( bitmap != null && ! newJob.isCanceled() ){

                        final int w = view.getWidth();
                        final int h = view.getHeight();

                        Bitmap scaledBitmap = resizeBitmap(bitmap, w, h);
                        bitmap.recycle();

                        setBitmap(view, scaledBitmap);

                        mMemoryCache.put(urlStr, scaledBitmap);
                    }else{
                        setErrorBitmap(view);
                    }
                } catch (IOException e) {
                    setErrorBitmap(view);
                }
            }
        });
    }


    private void setEmptyBitmap(ImageView view) {
        view.setImageDrawable(new ColorDrawable(Color.LTGRAY));
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
       Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        return scaledBitmap;
    }

    private void setBitmap(final ImageView view, final Bitmap bitmap) {
        mMainUIHandler.post(new Runnable() {

            @Override
            public void run() {
                view.setImageBitmap(bitmap);
            }

        });
    }

    private void setErrorBitmap(final ImageView view) {
        mMainUIHandler.post(new Runnable() {

            @Override
            public void run() {
                view.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            }

        });
    }

    private static class DownloadJob {

        private String mDownloadURL;
        private boolean mCanceled;

        public DownloadJob(String urlStr){
            mDownloadURL = urlStr;
        }

        public void cancel(){
            mCanceled = true;
        }

        public boolean isCanceled(){
            return mCanceled;
        }
    }
}
