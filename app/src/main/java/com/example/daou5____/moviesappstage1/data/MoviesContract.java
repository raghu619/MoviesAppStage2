package com.example.daou5____.moviesappstage1.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.example.daou5____.moviesappstage1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_MOVIE = "movies-path";

    public static final class FavoritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public final static String _ID = BaseColumns._ID;

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIEID = "movieid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RATING = "user_rating";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
    }
}