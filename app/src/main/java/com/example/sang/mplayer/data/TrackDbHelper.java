package com.example.sang.mplayer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bspl-hpl18 on 20/7/18.
 */

public class TrackDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mplayer.db";
    private static final int DATABASE_VERSION = 12;

    public TrackDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {



        final String SQL_CREATE_MOVIE_TABLE =

                "CREATE TABLE " + TrackContract.TABLE_NAME + " (" +
                        TrackContract._ID     + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        TrackContract.COLUMN_TID +" TEXT NOT NULL,"+

                        TrackContract.COLUMN_TITLE + " TEXT NOT NULL,"      +

                        TrackContract.COLUMN_ARTIST  + " TEXT NOT NULL, "     +
                        TrackContract.COLUMN_POSTER  + " TEXT NOT NULL, "                    +

                        TrackContract.COLUMN_PREVIEW_URL  + " TEXT NOT NULL, "                    +
                        TrackContract.COLUMN_ALBUM   + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TrackContract.TABLE_NAME);
        onCreate(db);
    }
}
