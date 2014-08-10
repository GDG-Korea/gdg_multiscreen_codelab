package com.gdgkoreaandroid.multiscreencodelab.tv;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;

import com.gdgkoreaandroid.multiscreencodelab.R;
import com.gdgkoreaandroid.multiscreencodelab.data.Movie;
import com.gdgkoreaandroid.multiscreencodelab.data.MovieList;

import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 *
 */
public class MovieBrowseFragment extends BrowseFragment {

    private ArrayObjectAdapter mRowsAdapter;
    private CardPresenter mCardPresenter;

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
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.primary_color));
    }

    private void setupAdapters() {
        MovieList.setupMovies();
        List<Movie> list = MovieList.MOVIE_LIST;

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        mCardPresenter = new CardPresenter();

        int i;
        for (i = 0; i < 3; i++) {
            if (i != 0) {
                Collections.shuffle(list);
            }

            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(mCardPresenter);
            for (int j = 0; j < list.size(); j++) {
                listRowAdapter.add(list.get(j));
            }

            HeaderItem header = new HeaderItem(i, "CATEGORY:" + (i + 1), null);
            mRowsAdapter.add(new ListRow(header, listRowAdapter));
        }

        setAdapter(mRowsAdapter);
    }
}
