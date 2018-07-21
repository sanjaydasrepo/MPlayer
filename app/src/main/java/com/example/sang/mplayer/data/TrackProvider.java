package com.example.sang.mplayer.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by bspl-hpl18 on 20/7/18.
 */

public class TrackProvider extends ContentProvider {

    public static final int CODE_ALL_TRACK = 100;
    public static final int CODE_TRACK_WITH_ID = 101;

    private static TrackDbHelper trackDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();




    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TrackContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TrackContract.PATH_TRACK, CODE_ALL_TRACK);
        matcher.addURI(authority, TrackContract.PATH_TRACK + "/#", CODE_TRACK_WITH_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        Context context = getContext() ;
        trackDbHelper = new TrackDbHelper( context ) ;
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = null;


        switch (sUriMatcher.match(uri)) {
            case CODE_ALL_TRACK:
                cursor = trackDbHelper.getReadableDatabase().query(
                        TrackContract.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case CODE_TRACK_WITH_ID:

                cursor = trackDbHelper.getReadableDatabase().query(
                        TrackContract.TABLE_NAME,
                        projection,
                        TrackContract.COLUMN_TID + "= ? ",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = trackDbHelper.getWritableDatabase();
        Uri iUri;

        switch (sUriMatcher.match(uri)) {
            case CODE_ALL_TRACK :
                db.beginTransaction();

                try {
                    long l = db.insert(TrackContract.TABLE_NAME ,null,values);

                    iUri = TrackContract.buildTrackUriWithId( String.valueOf(l) );


                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                return iUri;


        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = trackDbHelper.getWritableDatabase();
        int tasksDeleted;

        switch (sUriMatcher.match(uri)) {
            case CODE_TRACK_WITH_ID :
                db.beginTransaction();

                try {
                    tasksDeleted = db.delete(TrackContract.TABLE_NAME,
                            TrackContract.COLUMN_TID + " = ? ",
                            selectionArgs);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }


        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
