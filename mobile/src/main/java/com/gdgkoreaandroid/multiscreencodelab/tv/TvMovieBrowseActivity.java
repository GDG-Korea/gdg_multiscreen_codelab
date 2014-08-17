package com.gdgkoreaandroid.multiscreencodelab.tv;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.gdgkoreaandroid.multiscreencodelab.R;

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
    }
}
