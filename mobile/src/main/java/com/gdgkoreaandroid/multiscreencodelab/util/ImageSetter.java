package com.gdgkoreaandroid.multiscreencodelab.util;

import android.graphics.Bitmap;
import android.view.View;

public interface ImageSetter {

    Bitmap setImageBitmap(final Bitmap bitmap);
    void setErrorDrawable();
    void setEmptyDrawable();
    View getTargetView();

}
