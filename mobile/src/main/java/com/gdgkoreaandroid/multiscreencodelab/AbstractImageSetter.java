package com.gdgkoreaandroid.multiscreencodelab;

import android.view.View;

/**
 * Created by chansuk on 2014. 8. 10..
 */
public abstract class AbstractImageSetter implements ImageDownloader.ImageSetter {

    @Override
    public void setErrorDrawable() {

    }

    @Override
    public void setEmptyDrawable() {

    }

    @Override
    public View getTargetView() {
        return null;
    }
}
