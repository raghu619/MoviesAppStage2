package com.example.daou5____.moviesappstage1.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritesContract {

    public static final String CONTENT_AUTHORITY = "com.example.daou5____.moviesappstage1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_MOVIE = "movies-path";
    public static final String PATH_POPULAR = "popularity";
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_TOP_RATED = "top_rated";
    public static final String PATH_COMING_SOON = "upcoming";
    public static final String PATH_NOW_PLAYING = "now_playing ";

    public static final String COLUMN_MOVIE_ID_KEY = "movie_id";
    private FavoritesContract() {
    }

    public static final class FavoritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public final static String _ID = BaseColumns._ID;

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIEID = "movieid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RATING = "user_rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_OVERVIEW + " TEXT, " +
                        COLUMN_RELEASE_DATE + " TEXT, " +
                        COLUMN_POSTER_PATH + " TEXT, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_RATING + " REAL, " +
                        " );";

        private static final String[] COLUMNS = {_ID, COLUMN_OVERVIEW,
                COLUMN_RELEASE_DATE, COLUMN_POSTER_PATH, COLUMN_TITLE,};

        private FavoritesEntry() {
        }

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }

        public static String[] getColumns() {
            return COLUMNS.clone();
        }
    }

    public static final class PopularMovies implements BaseColumns {
        public static final Uri CONTENT_URI = FavoritesEntry.CONTENT_URI.buildUpon()
                .appendPath(PATH_POPULAR)
                .build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE
                        + "/" + PATH_POPULAR;

        public static final String TABLE_NAME = "most_popular_movies";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_MOVIE_ID_KEY + " INTEGER NOT NULL, " +

                        " FOREIGN KEY (" + COLUMN_MOVIE_ID_KEY + ") REFERENCES " +
                        FavoritesEntry.TABLE_NAME + " (" + FavoritesEntry._ID + ") " +

                        " );";

        private static final String[] COLUMNS = {_ID, COLUMN_MOVIE_ID_KEY};

        private PopularMovies() {
        }

        public static String[] getColumns() {
            return COLUMNS.clone();
        }
    }

    public static final class TopRated implements BaseColumns {
        public static final Uri CONTENT_URI = FavoritesEntry.CONTENT_URI.buildUpon()
                .appendPath(PATH_TOP_RATED)
                .build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE
                        + "/" + PATH_TOP_RATED;

        public static final String TABLE_NAME = "highest_rated_movies";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_MOVIE_ID_KEY + " INTEGER NOT NULL, " +

                        " FOREIGN KEY (" + COLUMN_MOVIE_ID_KEY + ") REFERENCES " +
                        FavoritesEntry.TABLE_NAME + " (" + FavoritesEntry._ID + ") " +

                        " );";

        private static final String[] COLUMNS = {_ID, COLUMN_MOVIE_ID_KEY};

        private TopRated() {
        }

        public static String[] getColumns() {
            return COLUMNS.clone();
        }
    }

    public static final class ComingSoon implements BaseColumns {
        public static final Uri CONTENT_URI = FavoritesEntry.CONTENT_URI.buildUpon()
                .appendPath(PATH_COMING_SOON)
                .build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE
                        + "/" + PATH_COMING_SOON;

        public static final String TABLE_NAME = "most_rated_movies";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_MOVIE_ID_KEY + " INTEGER NOT NULL, " +

                        " FOREIGN KEY (" + COLUMN_MOVIE_ID_KEY + ") REFERENCES " +
                        FavoritesEntry.TABLE_NAME + " (" + FavoritesEntry._ID + ") " +

                        " );";

        private static final String[] COLUMNS = {_ID, COLUMN_MOVIE_ID_KEY};

        private ComingSoon() {
        }

        public static String[] getColumns() {
            return COLUMNS.clone();
        }
    }

    public static final class NowPlaying implements BaseColumns {
        public static final Uri CONTENT_URI = FavoritesEntry.CONTENT_URI.buildUpon()
                .appendPath(PATH_NOW_PLAYING)
                .build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE
                        + "/" + PATH_NOW_PLAYING;

        public static final String TABLE_NAME = "most_rated_movies";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_MOVIE_ID_KEY + " INTEGER NOT NULL, " +

                        " FOREIGN KEY (" + COLUMN_MOVIE_ID_KEY + ") REFERENCES " +
                        FavoritesEntry.TABLE_NAME + " (" + FavoritesEntry._ID + ") " +

                        " );";

        private static final String[] COLUMNS = {_ID, COLUMN_MOVIE_ID_KEY};

        private NowPlaying() {
        }
    }

    public static final class Favorites implements BaseColumns {
        public static final Uri CONTENT_URI = FavoritesEntry.CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE
                        + "/" + PATH_FAVORITES;

        public static final String TABLE_NAME = "favorites";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_MOVIE_ID_KEY + " INTEGER NOT NULL, " +

                        " FOREIGN KEY (" + COLUMN_MOVIE_ID_KEY + ") REFERENCES " +
                        FavoritesEntry.TABLE_NAME + " (" + FavoritesEntry._ID + ") " +

                        " );";

        private static final String[] COLUMNS = {_ID, COLUMN_MOVIE_ID_KEY};

        private Favorites() {
        }

        public static String[] getColumns() {
            return COLUMNS.clone();
        }
    }
}