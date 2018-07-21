package com.example.sang.mplayer.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sang.mplayer.R;
import com.example.sang.mplayer.modal.Track;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackListViewHolder> {

    ArrayList<Track> trackList;
    Context context;

    OnTrackClickHandler onTrackClickHandler;
    public TrackAdapter(OnTrackClickHandler onTrackClickHandler) {
        this.onTrackClickHandler = onTrackClickHandler;
    }

    @NonNull
    @Override
    public TrackListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        this.context = context;

        int idTrackItem = R.layout.track_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(idTrackItem , parent , shouldAttachToParentImmediately);
        return new TrackListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListViewHolder holder, int i) {
        Picasso.with(context).load(trackList.get(i).getArtworkUrl60()).into(holder.ivTrackPoster);
        holder.trackTitle.setText(trackList.get(i).getTrackName());
        holder.trackArtist.setText(trackList.get(i).getArtistName());
        holder.trackCollection.setText(trackList.get(i).getCollectionName());
    }

    @Override
    public int getItemCount() {
        if( trackList != null )
            return trackList.size();

        return 0;
    }

    public void setTracks(ArrayList<Track> tracks){
        this.trackList = tracks;
        notifyDataSetChanged();
    }

    public interface OnTrackClickHandler {
         void onTrackClick(Track track);
    }


    class TrackListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.iv_track_poster)
        ImageView ivTrackPoster;

        @BindView(R.id.tv_track_title)
        TextView trackTitle;

        @BindView(R.id.tv_track_artist)
        TextView trackArtist;

        @BindView(R.id.tv_track_collection)
        TextView trackCollection;


        public TrackListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int i = getAdapterPosition();
            Track track = trackList.get(i);
            onTrackClickHandler.onTrackClick( track );
        }
    }
}
