package com.smojify.smojify;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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

    String smojifyUri = "31drfiwkyk7jftdlfccrnmylm5li";
    private String currentPlaylist;
    private SpotifyAppRemote appRemote;


    public SpotifyUtil() {
    }

    public void updatePlaylistState(String webToken, String playlistName, Bitmap cover, boolean isPublic, boolean isCollaborative, boolean isWorldWide, String trackUri, boolean first) {

        Log.d("Spotify API", "Updating playlist state for: " + playlistName);
        fetchPlaylists(webToken, playlistName,cover, isPublic, isCollaborative, isWorldWide, trackUri, first, "me");
        //fetchPlaylists(webToken, playlistName,cover, isPublic, isCollaborative, isWorldWide, trackUri, first, "users/"+smojifyUri);
    }

    private void fetchPlaylists(String webToken, String playlistTargetName, Bitmap cover, boolean isPublic, boolean isCollaborative, boolean isWorldWide, String trackUri, boolean first, String userUri) {
        new Thread(() -> {
            try {
                int offset = 0;
                int limit = 50;
                boolean found = false;
                boolean hasMorePlaylists = true;

                while (hasMorePlaylists) {
                    URL url = new URL(API_BASE_URL + "/" + userUri + "/playlists?limit=" + limit + "&offset=" + offset);
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
                            if (!found && userUri == "me") {
                                createPlaylist(webToken, playlistTargetName, cover, isPublic, isCollaborative, trackUri);
                                return ;
                            }
                        } else {
                            offset += limit;
                            Log.e("FetchPlaylits", userUri + " Offset:" + offset);
                            Thread.sleep(500); // Wait for 30 seconds
                        }
                    } else if (responseCode == 429) {
                        Log.e("Spotify API", "Rate limit exceeded. Fetching playlists. Waiting for 30 seconds before retrying...");
                        Thread.sleep(30000); // Wait for 30 seconds
                    } else {
                        Log.e("Spotify API", "Error fetching playlists: HTTP " + responseCode + " - " + connection.getResponseMessage());

                        // Attempt to log detailed error message from the response body, if available
                        InputStream errorStream = connection.getErrorStream();
                        if (errorStream != null) {
                            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                            StringBuilder errorResponse = new StringBuilder();
                            String errorLine;
                            while ((errorLine = errorReader.readLine()) != null) {
                                errorResponse.append(errorLine);
                            }
                            errorReader.close();

                            // Log the detailed error message from the server, if any
                            Log.e("Spotify API", "Error detail: " + errorResponse.toString());
                        } else {
                            Log.e("Spotify API", "No detailed error message available from the server.");
                        }

                        // Log the URL that caused the error for easier debugging
                        Log.e("Spotify API", "URL causing error: " + url);

                        // Additional context if needed, like request parameters
                        Log.e("Spotify API", "Request params - Limit: " + limit + ", Offset: " + offset);

                        // Indicate the action taken after the error
                        Log.e("Spotify API", "Aborting fetch due to error. Stopping further playlist fetch attempts.");

                        hasMorePlaylists = false; // Stop fetching playlists on error
                        return;
                    }

                    connection.disconnect();
                }

                // Playlist was not found, create it
                if (userUri ==  "me") {
                    createPlaylist(webToken, playlistTargetName, cover, isPublic, isCollaborative, trackUri);
                }
                // Call the updatePlaylistState function here
                if (first) {
                    updatePlaylistState(webToken, playlistTargetName, cover, isPublic, isCollaborative, isWorldWide, trackUri, false);
                    if (userUri == "me") {
                        fetchPlaylists(webToken, playlistTargetName,cover, isPublic, isCollaborative, isWorldWide, trackUri, first, "users/"+smojifyUri);
                    }
                }
            } catch (IOException | JSONException | InterruptedException e) {
                Log.e("Spotify API", "Failed to fetch playlists: " + e.getMessage());
            }
        }).start();
    }

    private Bitmap trimBitmap(Bitmap bmp) {
        int imgHeight = bmp.getHeight();
        int imgWidth  = bmp.getWidth();

        // Initialize to the extreme opposites
        int smallX = imgWidth, smallY = imgHeight, largeX = 0, largeY = 0;

        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                if (Color.alpha(bmp.getPixel(x, y)) > 0) {
                    if (x < smallX) smallX = x;
                    if (y < smallY) smallY = y;
                    if (x > largeX) largeX = x;
                    if (y > largeY) largeY = y;
                }
            }
        }

        // If the bitmap is entirely transparent, return the original one
        if (smallX >= largeX || smallY >= largeY) return bmp;

        // Calculate the width and height of the bounding box
        int boundingBoxWidth = largeX - smallX + 1;
        int boundingBoxHeight = largeY - smallY + 1;

        // The size of the square to create: the larger of the bounding box's width and height
        int squareSize = Math.max(boundingBoxWidth, boundingBoxHeight);

        // Create a square bitmap with the required size
        Bitmap squareBitmap = Bitmap.createBitmap(squareSize, squareSize, bmp.getConfig());

        // Calculate the position to draw the original bitmap on the square bitmap
        int drawX = (squareSize - boundingBoxWidth) / 2;
        int drawY = (squareSize - boundingBoxHeight) / 2;

        // Draw the original bitmap on the square bitmap
        Canvas canvas = new Canvas(squareBitmap);
        canvas.drawBitmap(bmp, -smallX + drawX, -smallY + drawY, null);

        // Return the square bitmap
        return squareBitmap;
    }




    public void createPlaylist(String webToken, String playlistName, Bitmap cover, boolean isPublic, boolean isCollaborative, String trackUri) {
        Log.e("SpotifyUtil", "Creating playlist: " + playlistName);
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                // Convert bitmap to base64
                Bitmap trimmedCover = trimBitmap(cover);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                trimmedCover.compress(Bitmap.CompressFormat.PNG, 90, stream);
                byte[] byteArr = stream.toByteArray();
                String base64Cover = Base64.encodeToString(byteArr, Base64.NO_WRAP);
                base64Cover = base64Cover.trim().replace("\n", "");
                String bodyContent;
                if (isCollaborative) {
                    if (isPublic) {
                        Log.w("Spotify API", "Collaborative playlists can only be private.");
                    }
                    bodyContent = "{\n\"name\":\"" + playlistName + "\",\n\"public\":" + false + ",\n\"collaborative\":" + true + "\n}";
                } else {
                    bodyContent = "{\n\"name\":\"" + playlistName + "\",\n\"public\":" + isPublic + ",\n\"collaborative\":" + false + "\n}";
                }
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, bodyContent);
                Log.e("SpotifyUtil", "Creating Playlist: " + bodyContent);
                Request request = new Request.Builder()
                        .url(API_BASE_URL + "/me/playlists")
                        .post(body)
                        .addHeader("Authorization", "Bearer " + webToken)
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = null;
                boolean shouldRetry = false;

                do {
                    if (shouldRetry) {
                        Log.e("Spotify API", "Rate limit exceeded. Waiting for 30 seconds before retrying...");
                        Thread.sleep(60000); // Wait for 30 seconds before retrying
                    }

                    response = client.newCall(request).execute();

                    if (response.code() == 429) {
                        shouldRetry = true;
                    } else {
                        shouldRetry = false;
                    }
                } while (shouldRetry);

                // Extract the playlist ID from the response
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                Log.w("SpotifyUtil", jsonResponse.toString());
                String playlistUri = jsonResponse.getString("uri");
                String playlistId = playlistUri.split(":")[2];

                Log.d("SpotifyUtil", "Created playlist: " + playlistName);
                fetchTrackAndAdd(webToken, playlistUri, "", trackUri);

                // Upload cover image
                MediaType imageMediaType = MediaType.parse("image/png");
                RequestBody coverBody = RequestBody.create(imageMediaType, base64Cover);
                Request coverRequest = new Request.Builder()
                        .url(API_BASE_URL + "/playlists/" + playlistId + "/images")
                        .put(coverBody)
                        .addHeader("Authorization", "Bearer " + webToken)
                        .addHeader("Content-Type", "image/png")
                        .build();

                Response coverResponse = client.newCall(coverRequest).execute();
                if (!coverResponse.isSuccessful()) {
                    Log.e("SpotifyUtil", "Failed to upload playlist cover. Response: " + coverResponse.body().string());
                } else {
                    Log.d("SpotifyUtil", "Uploaded playlist cover. Response: " + coverResponse.body().string());
                }

            } catch (IOException e) {
                Log.e("Spotify API", "Failed to create playlist: " + e.getMessage());
            } catch (JSONException e) {
                Log.e("Spotify API", "Failed to parse JSON response: " + e.getMessage());
            } catch (InterruptedException e) {
                Log.e("Spotify API", "Failed to wait for 30 seconds: " + e.getMessage());
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