package com.gdgkoreaandroid.multiscreencodelab.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public final class MovieList {

    /**
     * The argument representing the item ID that passed through fragments and activities.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final long INVALID_ID = -1;
    public static final String[] CATEGORY_LIST;

    static {
        CATEGORY_LIST = new String[]{ "Action", "Comedy", "Horror", "Romance", "Drama" };
    }

    public static final ArrayList<Movie> MOVIE_LIST;
    static {

        MOVIE_LIST = new ArrayList<Movie>();
        String title[] = {
                "Zeitgeist 2010_ Year in Review",
                "Google Demo Slam_ 20ft Search",
                "Introducing Gmail Blue",
                "Introducing Google Fiber to the Pole",
                "Introducing Google Nose"
        };

        String description = "Fusce id nisi turpis. Praesent viverra bibendum semper. "
                + "Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est "
                + "quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit "
                + "amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit "
                + "facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id "
                + "lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.";

        String videoUrl[] = {
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose.mp4"
        };
        String bgImageUrl[] = {
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review/bg.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search/bg.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue/bg.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole/bg.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose/bg.jpg",
        };
        String cardImageUrl[] = {
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose/card.jpg"
        };

        MOVIE_LIST.add(buildMovieInfo(title[0],
                description, CATEGORY_LIST[0], videoUrl[0], cardImageUrl[0], bgImageUrl[0]));
        MOVIE_LIST.add(buildMovieInfo(title[1],
                description, CATEGORY_LIST[1], videoUrl[1], cardImageUrl[1], bgImageUrl[1]));
        MOVIE_LIST.add(buildMovieInfo(title[2],
                description, CATEGORY_LIST[2], videoUrl[2], cardImageUrl[2], bgImageUrl[2]));
        MOVIE_LIST.add(buildMovieInfo(title[3],
                description, CATEGORY_LIST[3], videoUrl[3], cardImageUrl[3], bgImageUrl[3]));
        MOVIE_LIST.add(buildMovieInfo(title[4],
                description, CATEGORY_LIST[4], videoUrl[4], cardImageUrl[4], bgImageUrl[4]));
    }

    public static final HashMap< String, ArrayList<Movie> > CATEGORY_MOVIE_MAP;
    static {
        final Random rand = new Random();
        final int movieListSize = MOVIE_LIST.size();
        CATEGORY_MOVIE_MAP = new HashMap<String, ArrayList<Movie> >();

        ArrayList<Movie> tempList = new ArrayList<Movie>();
        tempList.addAll(MOVIE_LIST);

        for (String cateogry : CATEGORY_LIST) {
            Collections.shuffle(tempList);
            final int randomCount = 3 + rand.nextInt(5);
            ArrayList<Movie> movies = new ArrayList<Movie>();
            for (int j = 0; j < randomCount; ++j) {
                movies.add(tempList.get(j % movieListSize));
            }
            CATEGORY_MOVIE_MAP.put(cateogry, movies);
        }
    }

    public static Movie getMovie(long id) {

        for( Movie movie : MOVIE_LIST) {
            if( movie.getId() == id ) {
                return movie;
            }
        }
        return null;
    }

    public static Movie getPreviousMovie(Movie movie){
        long id = movie.getId() + Movie.getCount();
        Log.d("PreId", id-1+"");
        return getMovie((id - 1) % Movie.getCount());
    }

    public static Movie getNextMovie(Movie movie){
        long id = movie.getId();
        Log.d("NextId", id+1+"");
        return getMovie((id + 1) % Movie.getCount());
    }

    private static Movie buildMovieInfo(String title,
                                        String description, String studio, String videoUrl, String cardImageUrl,
                                        String bgImageUrl) {
        Movie movie = new Movie();
        movie.setId(Movie.getCount());
        Movie.incCount();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setStudio(studio);
        movie.setCardImageUrl(cardImageUrl);
        movie.setBackgroundImageUrl(bgImageUrl);
        movie.setVideoUrl(videoUrl);
        return movie;
    }
}