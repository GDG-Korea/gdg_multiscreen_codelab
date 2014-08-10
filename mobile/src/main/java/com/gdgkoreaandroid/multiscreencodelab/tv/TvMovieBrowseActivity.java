package com.gdgkoreaandroid.multiscreencodelab.tv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.OnItemClickedListener;
import android.support.v17.leanback.widget.Row;
import android.view.Menu;
import android.view.MenuItem;

import com.gdgkoreaandroid.multiscreencodelab.R;
import com.gdgkoreaandroid.multiscreencodelab.dummy.Movie;
import com.gdgkoreaandroid.multiscreencodelab.dummy.MovieList;

public class TvMovieBrowseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);
        BrowseFragment fragment = (BrowseFragment) getFragmentManager().findFragmentById(R.id.fragment_tv);
        fragment.setOnItemClickedListener(new OnItemClickedListener() {
            @Override
            public void onItemClicked(Object item, Row row) {
                if( item instanceof Movie ) {
                    long movie_id = ((Movie) item).getId();
                    Intent intent = new Intent(TvMovieBrowseActivity.this, TvMovieDetailsActivity.class);
                    intent.putExtra(MovieList.ARG_ITEM_ID, movie_id);
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
