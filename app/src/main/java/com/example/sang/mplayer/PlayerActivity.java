package com.example.sang.mplayer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sang.mplayer.data.TrackContract;
import com.example.sang.mplayer.modal.Track;
import com.example.sang.mplayer.utils.TrackUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.audio_view)
    PlayerView playerView;
    @BindView(R.id.tv_track_title)
    TextView tvTrackTitle;
    @BindView(R.id.tv_track_artist)
    TextView tvTrackArtist;
    @BindView(R.id.tv_track_collection)
    TextView tvTrackAlbum;

    @BindView(R.id.iv_fav_icon)
    ImageView ivFavIcon;

    @BindView(R.id.iv_list_tracks)
    ImageView ivListTracks;


    @BindView(R.id.iv_track_poster)
    ImageView ivTrackPoster;

    SimpleExoPlayer player;
    private int currentWindow;
    private boolean playWhenReady;
    private long playbackPosition;

    private static String previewurl;
    private static String trackTitle;
    private static String trackPoster;
    private static String trackArtist;
    private static String trackAlbum;
    private Context context;
    public static Track track;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        track = intent.getParcelableExtra(getString(R.string.trackselected));

        previewurl = track.getPreviewUrl();
        trackTitle = track.getTrackName();
        trackPoster = track.getArtworkUrl60();
        trackArtist = track.getArtistName();
        trackAlbum = track.getCollectionName();
        setTrackDetails();

        context = this ;
        ivFavIcon.setOnClickListener(this);
        ivListTracks.setOnClickListener(this);
        isTrackFavourite();
    }

    private void setTrackDetails() {
        tvTrackTitle.setText(trackTitle);
        tvTrackArtist.setText(trackArtist);
        tvTrackAlbum.setText(trackAlbum);
        Picasso.with(this).load(trackPoster).into(ivTrackPoster);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fav_action:
                displayFavList();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayFavList() {
        Intent intent = new Intent(this , FavActivity.class);
        startActivity( intent );
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());



        playerView.setPlayer(player);

        playerView.setControllerHideOnTouch(false);

        boolean playWhenReady = false;
        player.setPlayWhenReady(playWhenReady);
        int currentWindow = 0;
        int playbackPosition = 0;
        player.seekTo(currentWindow, playbackPosition);

        Uri uri = Uri.parse(previewurl);
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory(getString(R.string.exoplayer_codelab))).
                createMediaSource(uri);
    }
    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch ( id ){
            case R.id.iv_fav_icon:
                String tag = (String)  ivFavIcon.getTag();

                if(tag.equals(context.getString(R.string.FAV_TAG)) ){
                    unMarkTrackAsFavourite();
                }else {

                    markTrackAsFavourite();
                }

                break;
            case R.id.iv_list_tracks:
                onBackPressed();
                break;
        }
    }


    private void markTrackAsFavourite() {
        if(TrackUtils.markTrackAsFav( context , track))
            isTrackFavourite();
    }

    private void unMarkTrackAsFavourite() {

        if(TrackUtils.unMarkTrackAsFav( context , track))
            isTrackFavourite();
    }

    private  void isTrackFavourite(){
        new FetchTask().execute(context);

    }


    public class FetchTask extends AsyncTask<Context, Void, Cursor > {

        String tId = String.valueOf(track.getTrackId());

        String[] selectionArgs = new String[]{tId};


        @Override
        protected Cursor doInBackground(Context... params) {

            return params[0].getContentResolver().query(
                    TrackContract.buildTrackUriWithId(track.getTrackId()),
                    TrackUtils.FAVOURITE_TRACK_PROJECTION,
                    TrackContract.COLUMN_TID,
                    selectionArgs,
                    null);

        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if ( cursor != null ){
                if (cursor.moveToNext()){
                    setFavIcon();
                }

                else{
                    unsetFavIcon();
                }

            }
            else{
                unsetFavIcon();
            }
        }

        private void unsetFavIcon() {
            setIcon(R.drawable.shape_heart ,R.string.NOT_FAV_TAG);
        }

        private void setFavIcon() {
            setIcon(R.drawable.shape_heart_red ,R.string.FAV_TAG);
        }

        private void setIcon(final int imgResource ,final int tag){

           ivFavIcon.setImageResource(imgResource);
           ivFavIcon.setTag(context.getString(tag));

        }


    }



}
