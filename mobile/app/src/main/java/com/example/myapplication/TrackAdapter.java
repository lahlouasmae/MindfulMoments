package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {
    private List<SpotifyMusicActivity.MusicTrack> tracks;
    private OnTrackClickListener listener;

    public interface OnTrackClickListener {
        void onTrackClick(SpotifyMusicActivity.MusicTrack track);
    }

    public TrackAdapter(List<SpotifyMusicActivity.MusicTrack> tracks, OnTrackClickListener listener) {
        this.tracks = tracks;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_track, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SpotifyMusicActivity.MusicTrack track = tracks.get(position);

        holder.titleView.setText(track.title);
        holder.artistView.setText(track.artist);
        holder.imageView.setImageResource(track.imageResourceId);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrackClick(track);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleView;
        TextView artistView;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.track_image);
            titleView = view.findViewById(R.id.track_title);
            artistView = view.findViewById(R.id.track_artist);
        }
    }
}