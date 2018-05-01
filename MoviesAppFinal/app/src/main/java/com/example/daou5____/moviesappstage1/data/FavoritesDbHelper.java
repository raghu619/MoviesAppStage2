package com.example.daou5____.moviesappstage1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.daou5____.moviesappstage1.data.FavoritesContract.FavoritesEntry;
import com.example.daou5____.moviesappstage1.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";

    private static final int DATABASE_VERSION = 1;

    public static final String LOGTAG = "FAVORITES";
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
        Log.i(LOGTAG, "Database Opened");
    }

    public void close() {
        dbHelper.close();
        Log.i(LOGTAG, "Database Closed");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoritesContract.FavoritesEntry.TABLE_NAME + " (" +
                FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoritesEntry.COLUMN_MOVIEID + " INTEGER UNIQUE , " +
                FavoritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_RATING + " REAL NOT NULL, " +
                FavoritesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL" +
                "); ";

        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME);
        onCreate( db );
    }

    public void addFavorite(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FavoritesEntry.COLUMN_MOVIEID, movie.getId());
        values.put(FavoritesEntry.COLUMN_TITLE, movie.getOriginalTitle());
        values.put(FavoritesEntry.COLUMN_RATING, movie.getVoteAverage());
        values.put(FavoritesEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(FavoritesEntry.COLUMN_OVERVIEW, movie.getOverview());

        db.insert(FavoritesEntry.TABLE_NAME, null, values);
        db.close();
    }

    public void deleteFavorite(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FavoritesEntry.TABLE_NAME, FavoritesEntry.COLUMN_MOVIEID + "=" + id, null);
    }

    public List<Movie> getAllFavorite() {
        String[] columns = {
                FavoritesEntry._ID,
                FavoritesEntry.COLUMN_MOVIEID,
                FavoritesEntry.COLUMN_TITLE,
                FavoritesEntry.COLUMN_RATING,
                FavoritesEntry.COLUMN_POSTER_PATH,
                FavoritesEntry.COLUMN_OVERVIEW};

        String sortOrder = FavoritesEntry._ID + " ASC";
        List<Movie> favoritesList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FavoritesEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIEID))));
                movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_TITLE)));
                movie.setVoteAverage(Double.parseDouble(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_RATING))));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_POSTER_PATH)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_OVERVIEW)));

                favoritesList.add(movie);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return favoritesList;
    }
}