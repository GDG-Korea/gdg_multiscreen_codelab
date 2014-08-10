package com.gdgkoreaandroid.multiscreencodelab.tv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.gdgkoreaandroid.multiscreencodelab.AbstractImageSetter;
import com.gdgkoreaandroid.multiscreencodelab.MyApplication;
import com.gdgkoreaandroid.multiscreencodelab.PlayerActivity;
import com.gdgkoreaandroid.multiscreencodelab.R;
import com.gdgkoreaandroid.multiscreencodelab.dummy.Movie;
import com.gdgkoreaandroid.multiscreencodelab.dummy.MovieList;

import java.net.URI;

public class MovieDetailsFragment extends DetailsFragment {

    private static final String TAG = "VideoDetailsFragment";

    private static final int ACTION_WATCH_TRAILER = 1;

    private Movie mSelectedMovie;
    private DisplayMetrics mMetrics;
    private BackgroundManager mBackgroundManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);

        long movieId = getActivity().getIntent().getLongExtra(MovieList.ARG_ITEM_ID, 0);
        mSelectedMovie = MovieList.getMovie(movieId);

        setupAdapters();
        updateBackground(mSelectedMovie.getBackgroundImageURI());
    }

    private void setupAdapters() {

        DetailsOverviewRowPresenter dorPresenter =
                new DetailsOverviewRowPresenter(new AbstractDetailsDescriptionPresenter(){

                    @Override
                    protected void onBindDescription(ViewHolder viewHolder, Object item) {
                        Movie movie = (Movie) item;
                        if (movie != null) {
                            viewHolder.getTitle().setText(movie.getTitle());
                            viewHolder.getSubtitle().setText(movie.getStudio());
                            viewHolder.getBody().setText(movie.getDescription());
                        }

                    }
                });

        dorPresenter.setBackgroundColor(getResources().getColor(R.color.primary_dark_color));
        dorPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if (action.getId() == ACTION_WATCH_TRAILER) {
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra(MovieList.ARG_ITEM_ID, mSelectedMovie.getId());
                    intent.putExtra(getResources().getString(R.string.should_start), true);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ArrayObjectAdapter adapter = new ArrayObjectAdapter(dorPresenter);

        final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
        row.addAction(new Action(ACTION_WATCH_TRAILER, getResources().getString(
                R.string.watch_trailer_1), getResources().getString(R.string.watch_trailer_2)));

        adapter.add(row);
        setAdapter(adapter);

        updateBackground(mSelectedMovie.getBackgroundImageURI());
    }

    protected void updateBackground(URI uri) {
        Log.d(TAG, "uri" + uri);
        Log.d(TAG, "metrics" + mMetrics.toString());

        MyApplication.getImageDownloaderInstance().downloadImage(
                uri.toString(), new AbstractImageSetter() {

                    @Override
                    public Bitmap setImageBitmap(Bitmap bitmap) {

                        Bitmap resultBitmap = bitmap;

                        int width = mMetrics.widthPixels;
                        int height = mMetrics.heightPixels;

                        if(bitmap.getWidth() != width || bitmap.getHeight() != height){
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                                    bitmap, mMetrics.widthPixels, mMetrics.heightPixels, true);
                            bitmap.recycle();
                            resultBitmap = scaledBitmap;
                        }
                        mBackgroundManager.setBitmap(resultBitmap);
                        return resultBitmap;
                    }
                });
    }
}
