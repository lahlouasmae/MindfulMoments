package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SpotifyMusicActivity extends AppCompatActivity {
    private RecyclerView playlistRecyclerView;
    private List<MusicTrack> trackList;
    private TrackAdapter trackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_music);

        initializeViews();
        loadPlaylists();
    }

    private void initializeViews() {
        // Configuration du RecyclerView
        playlistRecyclerView = findViewById(R.id.playlist_recycler_view);
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        trackList = new ArrayList<>();
        trackAdapter = new TrackAdapter(trackList, this::openInSpotify);
        playlistRecyclerView.setAdapter(trackAdapter);
    }
    private void loadPlaylists() {
        // Playlists de méditation et relaxation
        trackList.add(new MusicTrack(
                "Mindfulness Meditation",
                "Daily Mindfulness",
                "spotify:track:0bXpmJyHHYPk6QBFj25bYF", // Mindfulness Meditation
                R.drawable.play
        ));





        trackList.add(new MusicTrack(
                "Delta Waves",
                "Sleep Therapy",
                "spotify:track:4EZz8Byhbjk0tOKFJlCgPB", // Delta Waves
                R.drawable.play
        ));






        // Données de la première playlist (Peaceful Piano)
        trackList.add(new MusicTrack(
                "Guided meditation",
                "Relaxation",
                "spotify:track:4uLU6hMCjMI75M1A2tKUQC", // URI valide
                R.drawable.play
        ));
        trackList.add(new MusicTrack(
                "Relaxing Piano",
                "Peaceful Piano",
                "spotify:track:6QgjcU0zLnzq5OrUoSZ3OK", // URI valide
                R.drawable.play
        ));

        // Données de la deuxième playlist (Nature Sounds)
        trackList.add(new MusicTrack(
                "Sounds of nature",
                "Nature Sounds",
                "spotify:track:7ouMYWpwJ422jRcDASZB7P", // URI valide
                R.drawable.play
        ));
        trackList.add(new MusicTrack(
                "Soft rain",
                "Nature Sounds",
                "spotify:track:1hKdDCpiI9mqz1jVHRKG0E", // URI valide
                R.drawable.play
        ));

        // Données de la playlist Anti-Stress
        trackList.add(new MusicTrack(
                "Calm vibes",
                "Anti-Stress",
                "spotify:track:4aWmUDTfIPGksMNLV2rQP2", // URI valide
                R.drawable.play
        ));

        // Données de la playlist Deep Focus
        trackList.add(new MusicTrack(
                "Deep Focus",
                "Concentration",
                "spotify:track:5DqdesEfbRyOlSS3Tf6c29", // URI valide
                R.drawable.play
        ));

        // Données de la playlist Classical Relaxation





        trackAdapter.notifyDataSetChanged();
    }


    private void openInSpotify(MusicTrack track) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(track.spotifyUri));
        intent.setPackage("com.spotify.music"); // Vérifie si Spotify est installé

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent); // Ouvrir dans Spotify
        } else {
            // Si Spotify n'est pas installé, ouvrir dans le navigateur
            String webUrl = "https://open.spotify.com/track/" +
                    track.spotifyUri.replace("spotify:track:", "");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
            startActivity(browserIntent);
        }
    }

    // Classe simple pour représenter une piste
    public static class MusicTrack {
        String title;
        String artist;
        String spotifyUri;
        int imageResourceId;

        public MusicTrack(String title, String artist, String spotifyUri, int imageResourceId) {
            this.title = title;
            this.artist = artist;
            this.spotifyUri = spotifyUri;
            this.imageResourceId = imageResourceId;
        }
    }
}