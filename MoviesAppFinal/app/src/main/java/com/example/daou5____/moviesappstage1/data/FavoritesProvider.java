package com.example.daou5____.moviesappstage1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.daou5____.moviesappstage1.data.FavoritesContract.FavoritesEntry;

public class FavoritesProvider extends ContentProvider {

    static final String LOG_TAG = FavoritesProvider.class.getSimpleName();

    static final int MOVIE = 100;
    static final int MOVIES_ID = 101;
    static final int FAVORITES = 300;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String FAILED_TO_INSERT_ROW_INTO = "Failed to insert row into ";

    private static final String MOVIE_ID_SELECTION =
            FavoritesEntry.TABLE_NAME + "." + FavoritesEntry._ID + " = ? ";

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoritesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, FavoritesContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(authority, FavoritesContract.PATH_MOVIE + "/#", MOVIES_ID);
        uriMatcher.addURI(authority, FavoritesContract.PATH_MOVIE + "/" +
                FavoritesContract.PATH_FAVORITES, FAVORITES);

        return uriMatcher;
    }

    /** Database helper object */
    private FavoritesDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new FavoritesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                cursor = database.query(FavoritesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MOVIES_ID:
                selection = FavoritesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(FavoritesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case FAVORITES:
                cursor = getMoviesFromReferenceTable(FavoritesContract.Favorites.TABLE_NAME,
                        projection, selection, selectionArgs, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }


        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public String getType( Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                return FavoritesEntry.CONTENT_DIR_TYPE;
            case MOVIES_ID:
                return FavoritesEntry.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return FavoritesContract.Favorites.CONTENT_DIR_TYPE;
            default:
                return null;
        }
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long id;
        switch (match) {
            case MOVIE:
                id = db.insertWithOnConflict(FavoritesEntry.TABLE_NAME, null,
                        values, SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0) {
                    returnUri = FavoritesEntry.buildMovieUri(id);
                } else {
                    throw new android.database.SQLException(FAILED_TO_INSERT_ROW_INTO + uri);
                }
                break;
            case FAVORITES:
                id = db.insert(FavoritesContract.Favorites.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = FavoritesContract.Favorites.CONTENT_URI;
                } else {
                    throw new android.database.SQLException(FAILED_TO_INSERT_ROW_INTO + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(FavoritesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIES_ID:
                long id = FavoritesEntry.getIdFromUri(uri);
                rowsDeleted = db.delete(FavoritesEntry.TABLE_NAME,
                        MOVIE_ID_SELECTION, new String[]{Long.toString(id)});
                break;
            case FAVORITES:
                rowsDeleted = db.delete(FavoritesContract.Favorites.TABLE_NAME, selection, selectionArgs);
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
        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(FavoritesEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long id = db.insertWithOnConflict(FavoritesEntry.TABLE_NAME,
                                null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {
        long id = FavoritesEntry.getIdFromUri(uri);
        String selection = MOVIE_ID_SELECTION;
        String[] selectionArgs = new String[]{Long.toString(id)};
        return mDbHelper.getReadableDatabase().query(
                FavoritesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMoviesFromReferenceTable(String tableName, String[] projection, String selection,
                                               String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        // tableName INNER JOIN movies ON tableName.movie_id = movies._id
        sqLiteQueryBuilder.setTables(
                tableName + " INNER JOIN " + FavoritesEntry.TABLE_NAME +
                        " ON " + tableName + "." + FavoritesContract.COLUMN_MOVIE_ID_KEY +
                        " = " + FavoritesEntry.TABLE_NAME + "." + FavoritesEntry._ID
        );

        return sqLiteQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
}


