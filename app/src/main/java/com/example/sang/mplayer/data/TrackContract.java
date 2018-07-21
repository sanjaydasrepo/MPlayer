package com.example.sang.mplayer.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by bspl-hpl18 on 20/7/18.
 */

public class TrackContract implements BaseColumns {
    public static final String CONTENT_AUTHORITY = "com.example.sang.mplayer";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_TRACK = "track";

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_TRACK)
            .build();

    public static final String TABLE_NAME = "fav_music";


    public static String COLUMN_TID = "track_id";
    public static String COLUMN_PREVIEW_URL = "preview_url";
    public static String COLUMN_TITLE = "title";
    public static String COLUMN_POSTER = "poster";
    public static String COLUMN_ARTIST = "artist";
    public static String COLUMN_ALBUM = "album";



    public static Uri buildTrackUriWithId(String id) {
        return CONTENT_URI.buildUpon()
                .appendPath(id)
                .build();
    }

}
