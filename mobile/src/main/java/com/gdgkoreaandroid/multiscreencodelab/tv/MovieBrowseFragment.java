package com.gdgkoreaandroid.multiscreencodelab.tv;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;

import com.gdgkoreaandroid.multiscreencodelab.R;
import com.gdgkoreaandroid.multiscreencodelab.data.MovieList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 *
 */
public class MovieBrowseFragment extends BrowseFragment{

    public MovieBrowseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupUIElements();
        setupAdapters();
    }

    private void setupUIElements() {
        //This method should be blank at the first, and be implemented by codelab attendees.
        setTitle(getString(R.string.browse_title));

        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        setBrandColor(getResources().getColor(R.color.primary_color));
    }

    private void setupAdapters() {

        ArrayObjectAdapter objectAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        for(String categoryName : MovieList.CATEGORY_LIST){
            HeaderItem headerItem = new HeaderItem(categoryName, null);

            ArrayObjectAdapter movies = new ArrayObjectAdapter(new StringPresenter());
            movies.addAll(0, MovieList.CATEGORY_MOVIE_MAP.get(categoryName));

            ListRow listRow = new ListRow(headerItem, movies);
            objectAdapter.add(listRow);
        }

        setAdapter(objectAdapter);
    }
}

