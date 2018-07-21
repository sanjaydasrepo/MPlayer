package com.example.sang.mplayer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Movie;
import android.net.Uri;
import android.widget.TableLayout;

import com.example.sang.mplayer.data.TrackContract;
import com.example.sang.mplayer.modal.Track;

import java.util.ArrayList;
import java.util.List;

public class TrackUtils {
    public static final String[] FAVOURITE_TRACK_PROJECTION = {
            TrackContract.COLUMN_TID,
            TrackContract.COLUMN_TITLE,
            TrackContract.COLUMN_ALBUM,
            TrackContract.COLUMN_PREVIEW_URL,
            TrackContract.COLUMN_POSTER,
            TrackContract.COLUMN_ARTIST
    } ;

    public static boolean markTrackAsFav(Context context, Track track) {
        ContentValues contentValues = new ContentValues();
        contentValues.put( TrackContract.COLUMN_TID , track.getTrackId());
        contentValues.put( TrackContract.COLUMN_TITLE ,  track.getTrackName());
        contentValues.put( TrackContract.COLUMN_ALBUM , track.getCollectionName());
        contentValues.put( TrackContract.COLUMN_PREVIEW_URL , track.getPreviewUrl());
        contentValues.put( TrackContract.COLUMN_POSTER,  track.getArtworkUrl60());
        contentValues.put( TrackContract.COLUMN_ARTIST , track.getArtistName());

        Uri uri = context.getContentResolver().insert( TrackContract.CONTENT_URI,
                contentValues);

        if( uri != null ){
            return true;
        }
        return false ;
    }

    public static boolean unMarkTrackAsFav(Context context, Track track) {
        String[] selectionArgs = new String[]{ track.getTrackId()};

        int res = context.getContentResolver().delete(
                TrackContract.buildTrackUriWithId(track.getTrackId()) ,
                TrackContract.COLUMN_TID,
                selectionArgs);


        if( res > 0 ) return true ;

        return false ;
    }


     public static List<Track> getListFromCursor(Cursor cursor) {
            List<Track> lTracks = new ArrayList<>();

            if( cursor !=null ){
                if (cursor.moveToFirst()){
                    do{
                        String tId = cursor.getString(cursor.getColumnIndex(TrackContract.COLUMN_TID));
                        String previewUrl = cursor.getString(cursor.getColumnIndex(TrackContract.COLUMN_PREVIEW_URL));
                        String title = cursor.getString(cursor.getColumnIndex(TrackContract.COLUMN_TITLE));
                        String poster = cursor.getString(cursor.getColumnIndex(TrackContract.COLUMN_POSTER));
                        String artist = cursor.getString(cursor.getColumnIndex(TrackContract.COLUMN_ARTIST));
                        String album = cursor.getString(cursor.getColumnIndex(TrackContract.COLUMN_ALBUM));

                        Track track = new Track();
                        track.setTrackId(tId);
                        track.setPreviewUrl(previewUrl);
                        track.setTrackName(title);
                        track.setArtworkUrl60(poster);
                        track.setArtistName(artist);
                        track.setCollectionName(album);

                        lTracks.add( track );

                    }while(cursor.moveToNext());
                }
                cursor.close();
            }
            return lTracks;
        }
    }

