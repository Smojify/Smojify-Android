package com.smojify.smojify;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.spotify.android.appremote.api.SpotifyAppRemote;

public class SmojifyService extends IntentService {

    private static final String ACTION_REACT_TO_TRACK = "com.smojify.smojify.action.REACT_TO_TRACK";
    private static final String EXTRA_EMOJI = "com.smojify.smojify.extra.EMOJI";
    private static final String EXTRA_TRACK_URI = "com.smojify.smojify.extra.TRACK_URI";
    private static final String EXTRA_SPOTIFY_WEB_TOKEN = "com.smojify.smojify.extra.SPOTIFY_WEB_TOKEN";
    private static final String ACTION_START_SMOJIFY = "com.smojify.smojify.action.START_SMOJIFY";
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
        context.startService(intent);
        Log.d("Smojify Service", "Service started correctly");
    }

    public void reactToTrack(Context context, String emoji, String trackUri, String token) {
        Intent intent = new Intent(context, SmojifyService.class);
        intent.setAction(ACTION_REACT_TO_TRACK);
        intent.putExtra(EXTRA_EMOJI, emoji);
        intent.putExtra(EXTRA_TRACK_URI, trackUri);
        intent.putExtra(EXTRA_SPOTIFY_WEB_TOKEN, token);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REACT_TO_TRACK.equals(action)) {
                final String emoji = intent.getStringExtra(EXTRA_EMOJI);
                final String trackUri = intent.getStringExtra(EXTRA_TRACK_URI);
                final String token = intent.getStringExtra(EXTRA_SPOTIFY_WEB_TOKEN);
                handleReactToTrack(emoji, trackUri, token);
            }
        }
    }

    private void handleReactToTrack(String emoji, String trackUri, String webToken) {
        Log.d("Smojify Service", "Reacting to track - Emoji: " + emoji + ", Track URI: " + trackUri);
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
                            spotifyAPI.updatePlaylistState(emojiName, webToken);
                    //spotifyAPI.updateTrackInPlaylist(false, trackUri, playlistUri, webToken);
                } else {
                    // Handle error or no data case
                    Log.d("Emoji Name", "Failed to fetch emoji name");
                }
            }
        });
    }
}
