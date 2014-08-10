package com.gdgkoreaandroid.multiscreencodelab.util;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.gdgkoreaandroid.multiscreencodelab.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageDownloader {

    private Handler mMainUIHandler;
    private ExecutorService mWorkerThreadPool;
    private LruCache<String, Bitmap> mMemoryCache;

    public ImageDownloader() {
        mMainUIHandler = new Handler(Looper.getMainLooper());
        mWorkerThreadPool = Executors.newFixedThreadPool(3);
        mMemoryCache = new LruCache<String, Bitmap>(30);
    }

    public DownloadJob downloadImage(final String urlStr, final ImageSetter imageSetter ){

        final URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            imageSetter.setEmptyDrawable();
            return null;
        }

        DownloadJob oldJob = null;

        final View targetView = imageSetter.getTargetView();
        if( targetView != null ) {
            oldJob = (DownloadJob) targetView.getTag(R.id.download_job);
        }

        //check current job.
        if( oldJob != null ){
            if(oldJob.mDownloadURL.equals(urlStr)) {
                return oldJob;
            }
            oldJob.cancel();
        }

        //check memory cache.
        Bitmap cachedBitmap = mMemoryCache.get(urlStr);
        if( cachedBitmap != null ){
            imageSetter.setImageBitmap(cachedBitmap);
            return null;
        }

        imageSetter.setEmptyDrawable();
        final DownloadJob newJob = new DownloadJob(urlStr);

        if(targetView != null) {
            targetView.setTag(R.id.download_job, newJob);
        }

        mWorkerThreadPool.execute(new Runnable(){

            @Override
            public void run() {

                try {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    final Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                    conn.disconnect();

                    if( bitmap != null && ! newJob.isCanceled() ){

                        mMainUIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap resultBitmap = imageSetter.setImageBitmap(bitmap);

                                if(resultBitmap != null) {
                                    mMemoryCache.put(urlStr, resultBitmap);
                                }
                            }
                        });
                    }else{

                        mMainUIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                imageSetter.setErrorDrawable();
                            }
                        });
                    }
                } catch (IOException e) {

                    mMainUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            imageSetter.setErrorDrawable();
                        }
                    });
                }
            }
        });

        return newJob;
    }

    public DownloadJob downloadImage(final String urlStr, final ImageView view){

        SimpleImageViewSetter imageSetter = new SimpleImageViewSetter(view);
        return downloadImage(urlStr, imageSetter);
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    final private class SimpleImageViewSetter implements ImageSetter {

        private final ImageView mView;

        private SimpleImageViewSetter(ImageView view) {
            mView = view;
        }

        @Override
        public void setEmptyDrawable() {
            mView.setImageDrawable(new ColorDrawable(Color.LTGRAY));
        }

        @Override
        public Bitmap setImageBitmap(final Bitmap bitmap) {

            final int width = mView.getMeasuredWidth();
            final int height = mView.getMeasuredHeight();

            if(width == 0 || height == 0){
                //view is not layouted.
                mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        final int width = mView.getMeasuredWidth();
                        final int height = mView.getMeasuredHeight();

                        Bitmap resultBitmap = bitmap;
                        if(bitmap.getWidth() < width || bitmap.getHeight() < height){
                            //the size of original bitmap is smaller than the target view.
                            resultBitmap = bitmap;
                        } else if(bitmap.getWidth() == width && bitmap.getHeight() == height) {
                            //the size of original bitmap is identical to the target view.
                            resultBitmap = bitmap;
                        } else {
                            Bitmap scaledBitmap = resizeBitmap(bitmap, width, height);
                            if(scaledBitmap != null) {
                                bitmap.recycle();
                                resultBitmap = scaledBitmap;
                            }
                        }

                        mView.setImageBitmap(resultBitmap);
                    }
                });

                return null;
            }

            Bitmap resultBitmap = bitmap;
            if(bitmap.getWidth() < width || bitmap.getHeight() < height){
                //the size of original bitmap is smaller than the target view.
                resultBitmap = bitmap;
            } else if(bitmap.getWidth() == width && bitmap.getHeight() == height) {
                //the size of original bitmap is identical to the target view.
                resultBitmap = bitmap;
            } else {
                Bitmap scaledBitmap = resizeBitmap(bitmap, width, height);
                if(scaledBitmap != null) {
                    bitmap.recycle();
                    resultBitmap = scaledBitmap;
                }
            }

            mView.setImageBitmap(resultBitmap);
            return resultBitmap;
        }

        @Override
        public void setErrorDrawable() {
            mView.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }

        @Override
        public View getTargetView() {
            return mView;
        }
    }

    public static class DownloadJob {

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
