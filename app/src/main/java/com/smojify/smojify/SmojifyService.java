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
    private static EmojiUtil emojiAPI;
    private static SpotifyUtil spotifyAPI;
    private static SpotifyAppRemote appRemote;

    public SmojifyService() {
        super("SmojifyService");
        emojiAPI = new EmojiUtil();
        spotifyAPI = new SpotifyUtil();
    }

    public static void reactToTrack(Context context, String emoji, String trackUri) {
        Intent intent = new Intent(context, SmojifyService.class);
        intent.setAction(ACTION_REACT_TO_TRACK);
        intent.putExtra(EXTRA_EMOJI, emoji);
        intent.putExtra(EXTRA_TRACK_URI, trackUri);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REACT_TO_TRACK.equals(action)) {
                final String emoji = intent.getStringExtra(EXTRA_EMOJI);
                final String trackUri = intent.getStringExtra(EXTRA_TRACK_URI);
                handleReactToTrack(emoji, trackUri);
            }
        }
    }

    private void handleReactToTrack(String emoji, String trackUri) {
        Log.d("SmojifyService", "Reacting to track - Emoji: " + emoji + ", Track URI: " + trackUri);

        // Retrieve Emoji Slug
        String emojiSlug = emojiAPI.getEmojiSlugName(emoji, new EmojiUtil.EmojiNameListener() {
            @Override
            public void onEmojiNameFetched(String emojiName) {
                if (emojiName != null) {
                    // Do something with the emojiName
                    Log.d("Emoji Name", emojiName);
                    Log.d("TrackUri", trackUri);
                    String playlistUri = spotifyAPI.updatePlaylistState(emojiName);
                    spotifyAPI.updateTrackInPlaylist(false, trackUri, playlistUri);
                } else {
                    // Handle error or no data case
                    Log.d("Emoji Name", "Failed to fetch emoji name");
                }
            }
        });
    }
}
