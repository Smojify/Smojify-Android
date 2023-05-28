package com.smojify.smojify;

import com.spotify.android.appremote.api.AppRemote;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.PlayerState;

public class SpotifyUtil {
    private static final String SMOJIFY_URI = "1234";
    private String userUri;
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
        if (appRemote != null && appRemote.isConnected()) {
        }
        return null;
    }
}
