package com.gdgkoreaandroid.multiscreencodelab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdgkoreaandroid.multiscreencodelab.dummy.Movie;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String TAG = "MovieAdapter";

    public MovieAdapter(Context context, List<Movie> movieList) {
        super(context, android.R.layout.simple_list_item_1);
        addAll(movieList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_movie_item, parent, false);

            ViewHolder holder = new ViewHolder();

            ImageView movieThumb = (ImageView) convertView.findViewById(R.id.movie_thumbnail);
            TextView movieTitle = (TextView) convertView.findViewById(R.id.movie_title);
            TextView movieDescription = (TextView) convertView.findViewById(R.id.movie_descritpion);

            holder.mMovieThumb = movieThumb;
            holder.mMovieTitle = movieTitle;
            holder.mMovieDescription = movieDescription;

            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();

        ImageView movieThumb = holder.mMovieThumb;
        TextView movieTitle = holder.mMovieTitle;
        TextView movieDescription = holder.mMovieDescription;

        Movie movie = getItem(position);

        ImageDownloader downloader = MyApplication.getImageDownloaderInstance();

        downloader.downloadImage(movie.getCardImageUrl(), movieThumb);
        movieTitle.setText(movie.getTitle());
        movieDescription.setText(movie.getDescription());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    private static class ViewHolder {

        private ImageView mMovieThumb;
        private TextView mMovieTitle;
        private TextView mMovieDescription;
    }
}
