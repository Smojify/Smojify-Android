package com.smojify.smojify;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.spotify.android.appremote.api.SpotifyAppRemote;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class SmojifyService extends IntentService {

    private static final String ACTION_REACT_TO_TRACK = "com.smojify.smojify.action.REACT_TO_TRACK";
    private static final String EXTRA_EMOJI = "com.smojify.smojify.extra.EMOJI";
    private static final String EXTRA_TRACK_URI = "com.smojify.smojify.extra.TRACK_URI";
    private static final String EXTRA_SPOTIFY_WEB_TOKEN = "com.smojify.smojify.extra.SPOTIFY_WEB_TOKEN";
    private static String EXTRA_IS_PUBLIC = "com.smojify.smojify.extra.IS_PUBLIC";
    private static final String ACTION_START_SMOJIFY = "com.smojify.smojify.action.START_SMOJIFY";
    private static final String EXTRA_EMOJI_BITMAP = "com.smojify.smojify.extra.EMOJI_BITMAP";

    private SharedPreferences sharedPreferences;

    private EmojiUtil emojiAPI;
    private SpotifyUtil spotifyAPI;
    private SpotifyAppRemote appRemote;
    private String spotifyWebToken;

    public SmojifyService() {
        super("SmojifyService");
        emojiAPI = new EmojiUtil();
        spotifyAPI = new SpotifyUtil();
    }

    public void startService(Context context) {
        Intent intent = new Intent(context, SmojifyService.class);
        intent.setAction(ACTION_START_SMOJIFY);
        //startOAuthServer();
        context.startService(intent);
        Log.d("Smojify Service", "Service started correctly");
    }

    private void startOAuthServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OAuthServer oauthServer = new OAuthServer();
                // Call the method to start the OAuthServer
                oauthServer.start();
            }
        }).start();
    }
    public void reactToTrack(Context context, String token, String emoji, Bitmap emojiBitmap, boolean isPublic, boolean isCollaborative, boolean isWorldWide, String trackUri) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        emojiBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        boolean[] playlistPrivacySettings ={isPublic, isCollaborative, isWorldWide};
        Log.e("Smojify", "public:" + String.valueOf(isPublic) + " collaborative: " + isCollaborative + " isWorld: " + isWorldWide);

        Intent intent = new Intent(context, SmojifyService.class);
        intent.setAction(ACTION_REACT_TO_TRACK);
        intent.putExtra(EXTRA_EMOJI, emoji);
        intent.putExtra(EXTRA_TRACK_URI, trackUri);
        intent.putExtra(EXTRA_SPOTIFY_WEB_TOKEN, token);
        intent.putExtra(EXTRA_IS_PUBLIC, playlistPrivacySettings);
        intent.putExtra(EXTRA_EMOJI_BITMAP, byteArray);  // change this
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REACT_TO_TRACK.equals(action)) {
                String emoji = intent.getStringExtra(EXTRA_EMOJI);
                String trackUri = intent.getStringExtra(EXTRA_TRACK_URI);
                String token = intent.getStringExtra(EXTRA_SPOTIFY_WEB_TOKEN);
                boolean[] playlistPrivacySettings = intent.getBooleanArrayExtra(EXTRA_IS_PUBLIC);
                boolean isPublic = playlistPrivacySettings[0];
                boolean isCollaborative = playlistPrivacySettings[1];
                boolean isWorldWide = playlistPrivacySettings[2];

                // Convert byte array back into Bitmap
                byte[] byteArray = intent.getByteArrayExtra(EXTRA_EMOJI_BITMAP);
                Bitmap emojiBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                Log.e("Smojify", "public:" + String.valueOf(isPublic) + " collaborative: " + isCollaborative + " isWorld: " + isWorldWide);
                handleReactToTrack(token, emoji, emojiBitmap, isPublic, isCollaborative, isWorldWide, trackUri);
            }
        }
    }

    private void handleReactToTrack(String webToken, String emoji,Bitmap cover,boolean isPublic,boolean isCollaborative,boolean isWorldWide,String trackUri) {
        sharedPreferences = getSharedPreferences("SmojifySettings", Context.MODE_PRIVATE);
        boolean createLocalPlaylist = sharedPreferences.getBoolean("createLocalPlaylist", false);
        boolean contributeGlobalPlaylist = sharedPreferences.getBoolean("contributeGlobalPlaylist", false);
        Log.d("Smojify Service", "Reacting to track - Emoji: " + emoji + ", Track URI: " + trackUri);

        if (contributeGlobalPlaylist) {
            sendReactionToSmojifyAPI(trackUri, emoji);
        } else {
            Log.d("Smojify Service", "Global playlist contribution is disabled");
        }

        if (createLocalPlaylist) {
            // Retrieve Emoji Slug
            String emojiSlug = emojiAPI.getEmojiSlugName(emoji, new EmojiUtil.EmojiNameListener() {
                @Override
                public void onEmojiNameFetched(String emojiName) {
                    if (emojiName != null) {
                        // Do something with the emojiName
                        Log.d("EmojiName", "Emoji Name: " + emojiName);
                        Log.d("EmojiName", "TrackUri: " + trackUri);
                        Log.d("EmojiName", "Token: " + webToken);
                        //String playlistUri =
                        spotifyAPI.updatePlaylistState(webToken, emojiName, cover, isPublic, isCollaborative, isWorldWide, trackUri, true);
                        //spotifyAPI.updateTrackInPlaylist(false, trackUri, playlistUri, webToken);
                    } else {
                        // Handle error or no data case
                        Log.d("Emoji Name", "Failed to fetch emoji name");
                    }
                }
            });
        } else {
            Log.d("Smojify Service", "Local playlist creation is disabled");
        }
    }
    private void sendReactionToSmojifyAPI(String trackUri, String emojiSlug) {
        new Thread(() -> {
            // Force the use of IPv4
            System.setProperty("java.net.preferIPv4Stack", "true");
            HttpURLConnection conn = null;

            try {
                InetAddress[] addresses = InetAddress.getAllByName("api.smojify.com");
                String ipv4Address = null;
                for (InetAddress address : addresses) {
                    if (address instanceof Inet4Address) {
                        ipv4Address = address.getHostAddress();
                        Log.d("DNS Test", "Using IPv4 Address: " + ipv4Address);
                        break; // Use the first IPv4 address found
                    }
                }

                if (ipv4Address == null) {
                    Log.e("Smojify Service", "No IPv4 address resolved for api.smojify.com");
                    return; // Exit the method if no IPv4 address is found
                }

                // Since we're manually resolving the IPv4 address, use it to create the URL
                URL url = new URL("http", "api.smojify.com", 4444, "/react");

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("trackUri", trackUri);
                jsonParam.put("emoji", emojiSlug);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonParam.toString().getBytes("UTF-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        Log.d("Smojify Service", "Response from Smojify API: " + response.toString());
                    }
                } else {
                    InputStream errorStream = conn.getErrorStream();
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    errorReader.close();
                    Log.e("Smojify Service", "Failed to send reaction to Smojify API. Response code: " + responseCode + ", Error: " + errorResponse.toString());
                }
            } catch (Exception e) {
                Log.e("Smojify Service", "Error sending reaction to Smojify API: " + e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }
}
