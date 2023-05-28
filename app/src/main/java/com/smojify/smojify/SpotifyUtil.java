package com.smojify.smojify;

import android.util.Log;
import android.view.View;

import com.spotify.android.appremote.api.AppRemote;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.PlayerState;

public class SpotifyUtil {
    private static final String SMOJIFY_URI = "1234";
    private String userUri;
    private String currentPlaylist;
    private SpotifyAppRemote appRemote;

    public SpotifyUtil(SpotifyAppRemote appRemote) {
        this.appRemote = appRemote;
    }

    public void updatePlaylistState(String playlistName) {
    }

    public void updateTrackInPlaylist(Boolean worldwide, String trackUri, String playlistName) {
        userUri = getUserUri();
        if (worldwide) {
            userUri = SMOJIFY_URI;
        }
    }

    private String getUserUri() {
        return "";
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
