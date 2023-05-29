package com.smojify.smojify;

import android.util.Log;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.spotify.android.appremote.api.SpotifyAppRemote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SpotifyUtil {
    private static final String API_BASE_URL = "https://api.spotify.com/v1";

    private static final String SMOJIFY_URI = "1234";
    private String userUri;
    private String currentPlaylist;
    private SpotifyAppRemote appRemote;


    public SpotifyUtil() {
    }

    public String updatePlaylistState(String playlistName, String webToken) {
        Log.e("SPOTIFYFETCH", webToken);
        Log.d("Spotify API", "Updating playlist state for: " + playlistName);
        fetchPlaylists(webToken);
        return playlistName;
    }

    public void updateTrackInPlaylist(Boolean worldwide, String trackUri, String playlistUri, String webToken) {
        Log.d("Spotify API", "Updating track state: " + trackUri + "\nIn playlist: " + playlistUri);
    }

    private void fetchPlaylists(String webToken) {
        new Thread(() -> {
            try {
                int offset = 0;
                int limit = 50;
                boolean hasMorePlaylists = true;

                while (hasMorePlaylists) {
                    URL url = new URL(API_BASE_URL + "/me/playlists?limit=" + limit + "&offset=" + offset);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + webToken);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder response = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        reader.close();
                        inputStream.close();

                        JSONObject responseJson = new JSONObject(response.toString());
                        JSONArray playlistsJson = responseJson.getJSONArray("items");

                        for (int i = 0; i < playlistsJson.length(); i++) {
                            JSONObject playlistJson = playlistsJson.getJSONObject(i);
                            String playlistName = playlistJson.getString("name");
                            // Process the retrieved playlist name
                            Log.d("SpotifyUtil", "Retrieved playlist: " + playlistName);
                        }

                        // Check if there are more playlists to fetch
                        if (playlistsJson.length() < limit) {
                            hasMorePlaylists = false;
                        } else {
                            offset += limit;
                        }
                    } else {
                        Log.e("Spotify API", "Failed to fetch playlists: " + responseCode + " - " + connection.getResponseMessage());
                        hasMorePlaylists = false; // Stop fetching playlists on error
                    }

                    connection.disconnect();
                }
            } catch (IOException | JSONException e) {
                Log.e("Spotify API", "Failed to fetch playlists: " + e.getMessage());
            }
        }).start();
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
