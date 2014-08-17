package com.gdgkoreaandroid.multiscreencodelab.tv;

import android.app.Fragment;
import android.os.Bundle;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 *
 */
public class MovieBrowseFragment extends Fragment{

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
    }

    private void setupAdapters() {
        //This method should be blank at the first, and be implemented by codelab attendees.
   }
}

