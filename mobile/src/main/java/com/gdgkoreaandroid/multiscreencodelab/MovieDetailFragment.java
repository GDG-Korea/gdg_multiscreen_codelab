package com.gdgkoreaandroid.multiscreencodelab;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdgkoreaandroid.multiscreencodelab.dummy.Movie;
import com.gdgkoreaandroid.multiscreencodelab.dummy.MovieList;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    /**
     * The dummy content this fragment is presenting.
     */
    private Movie mMovie;

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private CastDevice mSelectedDevice;
    private MediaRouteCallback mMediaRouteCallback;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMediaRouter = MediaRouter.getInstance(getActivity().getApplicationContext());
        mMediaRouteCallback = new MediaRouteCallback();

        setHasOptionsMenu(true);

        if (getArguments().containsKey(MovieList.ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            long id = getArguments().getLong(MovieList.ARG_ITEM_ID);
            mMovie = MovieList.getMovie(id);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mMovie != null) {

            ImageView thumbnail = (ImageView) rootView.findViewById(R.id.movie_detail_thumb);
            TextView title = (TextView) rootView.findViewById(R.id.movie_detail_title);
            TextView meta = (TextView) rootView.findViewById(R.id.movie_detail_meta);
            TextView description = (TextView) rootView.findViewById(R.id.movie_detail_descritpion);
            ImageView play = (ImageView) rootView.findViewById(R.id.movie_detail_play);
            View thumbContainer = rootView.findViewById(R.id.movie_detail_thumb_container);

            MyApplication.getImageDownloaderInstance().downloadImage(
                    mMovie.getBackgroundImageUrl(), thumbnail);

            title.setText(mMovie.getTitle());
            meta.setText(mMovie.getStudio());
            description.setText(mMovie.getDescription());

            play.setOnClickListener(mOnPlayVideoHandler);
            thumbContainer.setOnClickListener(mOnPlayVideoHandler);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        startDiscovery();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDiscovery();
    }

    private final View.OnClickListener mOnPlayVideoHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Play a video
            Intent intent = new Intent(v.getContext(), PlayerActivity.class);
            intent.putExtra(MovieList.ARG_ITEM_ID, mMovie.getId());
            intent.putExtra(v.getContext().getString(R.string.should_start), true);
            v.getContext().startActivity(intent);
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_movie_detail, menu);

        MenuItem castMenu = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(castMenu);
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
    }

    private void startDiscovery() {
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(
                        MyApplication.MEDIA_RECEIVER_APPLICATION_ID)).build();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouteCallback,
                MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
    }

    private void stopDiscovery() {
        mMediaRouter.removeCallback(mMediaRouteCallback);
        mMediaRouteSelector = null;
    }

    private class MediaRouteCallback extends MediaRouter.Callback {

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo route) {
            super.onRouteSelected(router, route);
            mSelectedDevice = CastDevice.getFromBundle(route.getExtras());

        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo route) {
            super.onRouteUnselected(router, route);
            mSelectedDevice = null;
        }
    }
}
