package com.gdgkoreaandroid.multiscreencodelab.tv;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;

import com.gdgkoreaandroid.multiscreencodelab.R;
import com.gdgkoreaandroid.multiscreencodelab.data.Movie;
import com.gdgkoreaandroid.multiscreencodelab.data.MovieList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class MovieDetailsFragment extends DetailsFragment {

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

        AbstractDetailsDescriptionPresenter detailsDescriptionPresenter
                = new AbstractDetailsDescriptionPresenter() {

            protected void onBindDescription(ViewHolder viewHolder, Object item) {
                Movie movie = (Movie) item;
                viewHolder.getTitle().setText(movie.getTitle());
                viewHolder.getSubtitle().setText(movie.getStudio());
                viewHolder.getBody().setText(movie.getDescription());
            }
        };

        DetailsOverviewRowPresenter detailsOverviewRowPresenter
                = new DetailsOverviewRowPresenter(detailsDescriptionPresenter);
        detailsOverviewRowPresenter.setOnActionClickedListener(new OnActionClickedListener(){

            @Override
            public void onActionClicked(Action action) {
                if (action.getId() == ACTION_WATCH_TRAILER) {
                    Intent intent = new Intent(getActivity(), TvPlayerActivity.class);
                    intent.putExtra(MovieList.ARG_ITEM_ID, mSelectedMovie.getId());
                    intent.putExtra(getResources().getString(R.string.should_start), true);
                    startActivity(intent);
                }
            }
        });

        detailsOverviewRowPresenter.setBackgroundColor(
                getResources().getColor(R.color.primary_color));

        DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
        row.setImageDrawable(mDefaultPreviewBitmap);
        row.addAction(new Action(ACTION_WATCH_TRAILER, "Watch Movie", "Free"));

        ArrayObjectAdapter adapter = new ArrayObjectAdapter(detailsOverviewRowPresenter);
        adapter.add(row);

        setAdapter(adapter);
    }

    private void updateBackground(URI uri) {
        //This method should be blank at the first, and be implemented by codelab attendees.
   }
}
