package com.gdgkoreaandroid.multiscreencodelab.util;

import android.view.View;

public abstract class DummyImageSetter implements ImageSetter {

    @Override
    public void setErrorDrawable() {
        //Do Nothing
    }

    @Override
    public void setEmptyDrawable() {
        //Do Nothing
    }

    @Override
    public View getTargetView() {
        //no target view
        return null;
    }
}
