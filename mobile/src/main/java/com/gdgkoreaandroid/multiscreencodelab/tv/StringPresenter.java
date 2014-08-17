package com.gdgkoreaandroid.multiscreencodelab.tv;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gdgkoreaandroid.multiscreencodelab.R;
import com.gdgkoreaandroid.multiscreencodelab.data.Movie;

/**
 * Simple presenter, which binds a {@link com.gdgkoreaandroid.multiscreencodelab.data.Movie}
 * to a plain {@link android.widget.TextView}
 */
public class StringPresenter extends Presenter {

    private static final int STRING_WIDTH = 320;
    private static final int STRING_HEIGHT = 240;

    private static class ViewHolder extends Presenter.ViewHolder {

        private TextView mTextView;

        ViewHolder(View view) {
            super(view);
            mTextView= (TextView) view;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Context context = parent.getContext();
        TextView textView = new TextView(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(STRING_WIDTH, STRING_HEIGHT));
        textView.setGravity(Gravity.CENTER);
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
        textView.setBackgroundColor(context.getResources().getColor(R.color.primary_color));
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Movie movie = (Movie) item;
        ((ViewHolder) viewHolder).mTextView.setText(movie.getTitle());
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
    }
}
