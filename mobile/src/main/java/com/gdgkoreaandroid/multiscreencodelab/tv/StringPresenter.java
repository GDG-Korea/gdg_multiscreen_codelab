package com.gdgkoreaandroid.multiscreencodelab.tv;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gdgkoreaandroid.multiscreencodelab.R;
import com.gdgkoreaandroid.multiscreencodelab.data.Movie;

public class StringPresenter extends Presenter {
    private static final String TAG = "StringPresenter";

    private static final int STRING_WIDTH = 320;
    private static final int STRING_HEIGHT = 240;

    static class ViewHolder extends Presenter.ViewHolder {
        private Movie mMovie;
        private TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mTextView= (TextView) view;
        }

        public void setMovie(Movie m) {
            mMovie = m;
        }
        public Movie getMovie() {
            return mMovie;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Log.d(TAG, "onCreateViewHolder");
        Context context = parent.getContext();
        TextView textView = new TextView(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(STRING_WIDTH, STRING_HEIGHT));
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
        textView.setBackgroundColor(context.getResources().getColor(R.color.primary_dark_color));
        textView.setGravity(Gravity.CENTER);
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Movie movie = (Movie) item;
        ((ViewHolder) viewHolder).setMovie(movie);

        Log.d(TAG, "onBindViewHolder");
        ((ViewHolder) viewHolder).mTextView.setText(movie.getTitle());
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        Log.d(TAG, "onUnbindViewHolder");
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
        Log.d(TAG, "onViewAttachedToWindow");
    }
}
