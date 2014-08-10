package com.gdgkoreaandroid.multiscreencodelab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.gdgkoreaandroid.multiscreencodelab.data.MovieList;

/**
 * An activity representing a MOVIE_LIST of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a MOVIE_LIST of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the MOVIE_LIST of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The MOVIE_LIST of items is a
 * {@link MovieListFragment} and the item details
 * (if present) is a {@link MovieDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link MovieListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class MovieListActivity extends Activity
        implements MovieListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);



        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, MOVIE_LIST items should be given the
            // 'activated' state when touched.
            ((MovieListFragment) getFragmentManager()
                    .findFragmentById(R.id.movie_list))
                    .setActivateOnItemClick(true);
        }
    }

    /**
     * Callback method from {@link MovieListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(long id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putLong(MovieList.ARG_ITEM_ID, id);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtra(MovieList.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
