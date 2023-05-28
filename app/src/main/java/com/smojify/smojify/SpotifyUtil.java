package com.smojify.smojify;

import android.util.Log;

import com.spotify.android.appremote.api.SpotifyAppRemote;

public class SpotifyUtil {
    private static final String API_BASE_URL = "https://api.spotify.com/v1";

    private static final String SMOJIFY_URI = "1234";
    private String userUri;
    private String currentPlaylist;
    private SpotifyAppRemote appRemote;

    public SpotifyUtil() {
    }

    public String updatePlaylistState(String playlistName) {
        Log.d("SpotifyAPI", "Updating playlist state for: " + playlistName);
        return playlistName;
    }

    public void updateTrackInPlaylist(Boolean worldwide, String trackUri, String playlistUri) {
        Log.d("SpotifyAPI", "Updating track state: " + trackUri + "\nIn playlist: " + playlistUri);
    }
    private void fetchPlaylists() {
    }

    public void removeTrackFromPlaylist() {

    }

    public void getTrackPosition() {

    }

    public void addTrackToPlaylist() {

    }
    public void getCurrentPlaylist() {
        appRemote.getPlayerApi().subscribeToPlayerContext().setEventCallback(playerContext -> {
            // Retrieve the current playlist from playerContext
            Log.d("SpotifyUtil", "Current playlist: " + playerContext.title);
            currentPlaylist = playerContext.title;
        });
    }

    public void createPlaylist() {

    }

    public String getCurrentPlaylistName() {
        return this.currentPlaylist;
    }

    public void reactToTrack(String trackUri, String emoji) {
    }
}
