package com.example.daou5____.moviesappstage1.data;

import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.daou5____.moviesappstage1.DataActivity;
import com.example.daou5____.moviesappstage1.data.MoviesContract.FavoritesEntry;
import com.example.daou5____.moviesappstage1.model.Movie;

public class MoviesProvider extends ContentProvider {

    static final String LOG_TAG = MoviesProvider.class.getSimpleName();

    static final int FAVORITES = 100;
    static final int FAVORITES_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    /**
     * Database helper object
     */
    private MoviesDbHelper mDbHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIE, FAVORITES);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIE + "/*", FAVORITES_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
                cursor = mDbHelper.getReadableDatabase().query(
                        FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVORITES_ID:
                cursor = mDbHelper.getReadableDatabase().query(
                        FavoritesEntry.TABLE_NAME,
                        projection,
                        FavoritesEntry._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Method not implemented for this database");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITES:
                long id = db.insert(FavoritesEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoritesEntry.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;
        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
            rowsDeleted = mDbHelper.getWritableDatabase().delete(FavoritesEntry.TABLE_NAME, selection, selectionArgs);
            break;
            case FAVORITES_ID:
                String id = uri.getPathSegments().get(1);
                String[] newSelectionArgs = new String[]{id};
                rowsDeleted = mDbHelper.getWritableDatabase().delete(FavoritesEntry.TABLE_NAME,
                        FavoritesEntry._ID + " = ?",
                        newSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        if (values == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }
        switch (match) {
            case FAVORITES:
                rowsUpdated = db.update(FavoritesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case FAVORITES_ID:
                rowsUpdated = db.update(FavoritesEntry.TABLE_NAME, values, FavoritesEntry._ID + " = ?", new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
