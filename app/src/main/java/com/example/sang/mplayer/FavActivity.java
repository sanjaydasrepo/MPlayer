package com.example.sang.mplayer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import com.example.sang.mplayer.data.TrackAdapter;
import com.example.sang.mplayer.data.TrackContract;
import com.example.sang.mplayer.modal.Track;
import com.example.sang.mplayer.utils.TrackUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavActivity extends AppCompatActivity implements TrackAdapter.OnTrackClickHandler , LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.rv_fav_list)
    RecyclerView rvFavList;

    @BindView(R.id.tv_fav_count)
    TextView tvFavCount;

    private Context context;
    private TrackAdapter adapter;
    ArrayList<Track> trackList;
    private static final int FAV_LOADER_ID = 123;
    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = getApplicationContext();
        LinearLayoutManager layoutManager =
                new LinearLayoutManager( this ,LinearLayoutManager.VERTICAL, false);

        rvFavList.setLayoutManager( layoutManager );

        adapter = new TrackAdapter( this);
        rvFavList.setAdapter( adapter );

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                // TODO
                //Yet to implement

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // view the background view
            }


        };

// attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvFavList);

        if(isOnline()) {
            getSupportLoaderManager().initLoader(FAV_LOADER_ID, null, this);
        }else{
          //  showErrorMessage(R.string.network_error);
        }
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
    public void onTrackClick(Track track) {
        Intent intent = new Intent(this , PlayerActivity.class);
        intent.putExtra(getString(R.string.trackselected) , track );
        startActivity( intent );
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackList = null ;
        getSupportLoaderManager().restartLoader(FAV_LOADER_ID, null, this);
    }

    private boolean isOnline(){
        NetworkInfo netInfo=null;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm != null)
            netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case FAV_LOADER_ID:
                Uri trackUri = TrackContract.CONTENT_URI;

                return new CursorLoader(this,
                        trackUri,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if( trackList == null )
            trackList = (ArrayList<Track>) TrackUtils.getListFromCursor(data);

        if( trackList.size() > 0){
            tvFavCount.setText( trackList.size()+"" );

            adapter.setTracks( trackList );
        }else {
            showErrorMessage(R.string.no_favourites);
        }

        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        rvFavList.smoothScrollToPosition(mPosition);
    }

    private void showErrorMessage(int no_favourites) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
