package com.gdgkoreaandroid.multiscreencodelab.tv;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.gdgkoreaandroid.multiscreencodelab.R;
import com.gdgkoreaandroid.multiscreencodelab.data.Movie;
import com.gdgkoreaandroid.multiscreencodelab.data.MovieList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class MovieDetailsFragment extends Fragment {

    private static final int ACTION_WATCH_TRAILER = 1;

    private Movie mSelectedMovie;
    private Drawable mDefaultPreviewBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long movieId = getActivity().getIntent().getLongExtra(MovieList.ARG_ITEM_ID, 0);
        mSelectedMovie = MovieList.getMovie(movieId);

        try {
            InputStream is = getActivity().getAssets().open("tv_users.jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            mDefaultPreviewBitmap = new BitmapDrawable(getResources(), bitmap);
            is.close();
        } catch (IOException e) {
            mDefaultPreviewBitmap
                    = new ColorDrawable(getResources().getColor(R.color.primary_dark_color));
        }

        setupAdapters();
        //TODO Bonus Point!
        //updateBackground(mSelectedMovie.getBackgroundImageURI());
    }

    private void setupAdapters() {

    }

    private void updateBackground(URI uri) {
    }
}
