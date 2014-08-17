package com.gdgkoreaandroid.multiscreencodelab.tv;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.OnItemClickedListener;
import android.support.v17.leanback.widget.Row;

import com.gdgkoreaandroid.multiscreencodelab.R;
import com.gdgkoreaandroid.multiscreencodelab.data.Movie;
import com.gdgkoreaandroid.multiscreencodelab.data.MovieList;

/**
 * Activtiy which hosts TvMovieBrowseFragment
 */
public class TvMovieBrowseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_fragment_dock);

        Fragment frag = new MovieBrowseFragment();
        FragmentTransaction transition = getFragmentManager().beginTransaction();
        transition.add(R.id.fragment_dock, frag, "MovieBrowseFragment");
        transition.commit();

        ((BrowseFragment)frag).setOnItemClickedListener(new OnItemClickedListener() {
            @Override
            public void onItemClicked(Object item, Row row) {
                if( item instanceof Movie) {
                    long movie_id = ((Movie) item).getId();
                    Intent intent = new Intent(TvMovieBrowseActivity.this, TvMovieDetailsActivity.class);
                    intent.putExtra(MovieList.ARG_ITEM_ID, movie_id);
                    startActivity(intent);
                }
            }
        });

    }
}
