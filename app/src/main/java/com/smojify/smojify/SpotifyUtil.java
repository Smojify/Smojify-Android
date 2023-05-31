package com.smojify.smojify;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.spotify.android.appremote.api.SpotifyAppRemote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SpotifyUtil {
    private static final String API_BASE_URL = "https://api.spotify.com/v1";

    private static final String SMOJIFY_URI = "1234";
    private String userUri;
    private String currentPlaylist;
    private SpotifyAppRemote appRemote;


    public SpotifyUtil() {
    }

    public void updatePlaylistState(String webToken, String playlistName, Bitmap cover, boolean isPublic, boolean isCollaborative, boolean isWorldWide, String trackUri, boolean first) {

        Log.e("SPOTIFYFETCH", webToken);
        Log.d("Spotify API", "Updating playlist state for: " + playlistName);
        fetchPlaylists(webToken, playlistName,cover, isPublic, isCollaborative, isWorldWide, trackUri, first);
    }

    private void fetchPlaylists(String webToken, String playlistTargetName, Bitmap cover, boolean isPublic, boolean isCollaborative, boolean isWorldWide, String trackUri, boolean first) {
        new Thread(() -> {
            try {
                int offset = 0;
                int limit = 50;
                boolean found = false;
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
                            String playlistSnapId = playlistJson.getString("snapshot_id");
                            String playlistUri = playlistJson.getString("uri");
                            Log.d("SpotifyUtil", "Retrieved playlist: " + playlistName);
                            if (playlistName.equals(playlistTargetName)) {
                                found = true;
                                Log.d("SpotifyUtil", "Found playlist: " + playlistName);
                                fetchTrackAndAdd(webToken, playlistUri, playlistSnapId, trackUri);
                                return;
                            }
                        }

                        if (playlistsJson.length() < limit && first) {
                            hasMorePlaylists = false;
                            if (!found) {
                                createPlaylist(webToken, playlistTargetName, cover, isPublic, isCollaborative, trackUri);
                                return ;
                            }
                        } else {
                            offset += limit;
                        }
                    } else {
                        Log.e("Spotify API", "Failed to fetch playlists: " + responseCode + " - " + connection.getResponseMessage());
                        hasMorePlaylists = false; // Stop fetching playlists on error
                    }

                    connection.disconnect();
                }

                // Playlist was not found, create it
                createPlaylist(webToken, playlistTargetName, cover, isPublic, isCollaborative, trackUri);

                // Call the updatePlaylistState function here
                if (first) {
                    updatePlaylistState(webToken, playlistTargetName, cover, isPublic, isCollaborative, isWorldWide, trackUri, false);
                }
            } catch (IOException | JSONException e) {
                Log.e("Spotify API", "Failed to fetch playlists: " + e.getMessage());
            }
        }).start();
    }


    public void createPlaylist(String webToken, String playlistName, Bitmap cover, boolean isPublic, boolean isCollaborative, String trackUri) {
        Log.e("SpotifyUtil", "Creating playlist: " + playlistName);
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                // Convert bitmap to base64
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                cover.compress(Bitmap.CompressFormat.PNG, 90, stream);
                byte[] byteArr = stream.toByteArray();
                String base64Cover = Base64.encodeToString(byteArr, Base64.NO_WRAP);

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\n\"name\":\"" + playlistName + "\",\n\"public\":" + isPublic + ",\n\"collaborative\":" + isCollaborative + "\n}");
                Request request = new Request.Builder()
                        .url(API_BASE_URL + "/me/playlists")
                        .post(body)
                        .addHeader("Authorization", "Bearer " + webToken)
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();

                // Extract the playlist ID from the response
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                String playlistUri = jsonResponse.getString("uri");
                String playlistId = playlistUri.split(":")[2];

                Log.d("SpotifyUtil", "Created playlist: " + playlistName);
                fetchTrackAndAdd(webToken, playlistUri, "", trackUri);

                // Upload cover image
                MediaType imageMediaType = MediaType.parse("image/png");
                RequestBody coverBody = RequestBody.create(imageMediaType, byteArr);
                Request coverRequest = new Request.Builder()
                        .url(API_BASE_URL + "/playlists/" + playlistId + "/images")
                        .put(coverBody)
                        .addHeader("Authorization", "Bearer " + webToken)
                        .addHeader("Content-Type", "image/png")
                        .build();

                Response coverResponse = client.newCall(coverRequest).execute();
                Log.d("SpotifyUtil", "Uploaded playlist cover");

            } catch (IOException e) {
                Log.e("Spotify API", "Failed to create playlist: " + e.getMessage());
            } catch (JSONException e) {
                Log.e("Spotify API", "Failed to parse JSON response: " + e.getMessage());
            }
        }).start();
    }

    public void fetchTrackAndAdd(String webToken, String playlistUri, String snapshotId, String trackUri) {
        new Thread(() -> {
            try {
                String playlistId = playlistUri.split(":")[2];
                OkHttpClient client = new OkHttpClient();

                // Retrieve the playlist tracks
                Request getTracksRequest = new Request.Builder()
                        .url(API_BASE_URL + "/playlists/" + playlistId + "/tracks")
                        .get()
                        .addHeader("Authorization", "Bearer " + webToken)
                        .build();

                Response getTracksResponse = client.newCall(getTracksRequest).execute();
                if (getTracksResponse.isSuccessful()) {
                    String responseString = getTracksResponse.body().string();
                    JSONObject responseObject = new JSONObject(responseString);
                    JSONArray tracksArray = responseObject.getJSONArray("items");

                    int position = -1;

                    // Find the track in the playlist and retrieve its position
                    for (int i = 0; i < tracksArray.length(); i++) {
                        JSONObject trackObject = tracksArray.getJSONObject(i);
                        JSONObject track = trackObject.getJSONObject("track");
                        String uri = track.getString("uri");
                        if (uri.equals(trackUri)) {
                            position = i;
                            break;
                        }
                    }

                    // Remove the track if it's in the playlist
                    if (position != -1) {
                        // Decrease the position by one if it's not at position 0
                        if (position > 0) {
                            position--;
                        }

                        JSONObject requestBody = new JSONObject();
                        JSONArray removeTracksArray = new JSONArray();
                        JSONObject trackObject = new JSONObject();
                        trackObject.put("uri", trackUri);
                        removeTracksArray.put(trackObject);
                        requestBody.put("tracks", removeTracksArray);
                        requestBody.put("snapshot_id", snapshotId);

                        MediaType mediaType = MediaType.parse("application/json");
                        RequestBody body = RequestBody.create(mediaType, requestBody.toString());


                        Request removeTrackRequest = new Request.Builder()
                                .url(API_BASE_URL + "/playlists/" + playlistId + "/tracks")
                                .delete(body)
                                .addHeader("Authorization", "Bearer " + webToken)
                                .addHeader("Content-Type", "application/json")
                                .build();

                        Response removeTrackResponse = client.newCall(removeTrackRequest).execute();
                        String removeTrackResponseBody = removeTrackResponse.body().string();
                        if (!removeTrackResponse.isSuccessful()) {
                            Log.e("SpotifyUtil", "Failed to remove track from playlist: " + removeTrackResponseBody);
                            return;
                        }
                    } else {
                        position = 0;
                    }

                    // Add the track to the playlist at the updated position
                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType, "{\n\"uris\":[\"" + trackUri + "\"],\n\"position\":" + position + "\n}");
                    Request addTrackRequest = new Request.Builder()
                            .url(API_BASE_URL + "/playlists/" + playlistId + "/tracks")
                            .post(body)
                            .addHeader("Authorization", "Bearer " + webToken)
                            .addHeader("Content-Type", "application/json")
                            .build();

                    Response addTrackResponse = client.newCall(addTrackRequest).execute();
                    if (addTrackResponse.isSuccessful()) {
                        Log.d("SpotifyUtil", "Added track to playlist: " + playlistId);
                    } else {
                        String addTrackResponseBody = addTrackResponse.body().string();
                        Log.e("SpotifyUtil", "Failed to add track to playlist: " + addTrackResponseBody);
                    }
                } else {
                    throw new IOException("Failed to retrieve playlist tracks: status code = " + getTracksResponse.code());
                }

            } catch (IOException e) {
                Log.e("Spotify API", "Failed to add track to playlist: " + e.getMessage());
            } catch (JSONException e) {
                Log.e("Spotify API", "Failed to parse playlist tracks response: " + e.getMessage());
            }
        }).start();
    }
}