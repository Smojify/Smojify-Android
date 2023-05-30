package com.smojify.smojify;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.io.ByteArrayOutputStream;

public class SmojifyService extends IntentService {

    private static final String ACTION_REACT_TO_TRACK = "com.smojify.smojify.action.REACT_TO_TRACK";
    private static final String EXTRA_EMOJI = "com.smojify.smojify.extra.EMOJI";
    private static final String EXTRA_TRACK_URI = "com.smojify.smojify.extra.TRACK_URI";
    private static final String EXTRA_SPOTIFY_WEB_TOKEN = "com.smojify.smojify.extra.SPOTIFY_WEB_TOKEN";
    private static final String EXTRA_IS_PUBLIC = "com.smojify.smojify.extra.IS_PUBLIC";
    private static final String EXTRA_IS_COLLABORATIVE = "com.smojify.smojify.extra.IS_COLLABORATIVE";
    private static final String EXTRA_IS_WORLD_WIDE = "com.smojify.smojify.extra.IS_WORLD_WIDE";

    private static final String ACTION_START_SMOJIFY = "com.smojify.smojify.action.START_SMOJIFY";
    private static final String EXTRA_EMOJI_BITMAP = "com.smojify.smojify.extra.EMOJI_BITMAP";

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

    public void reactToTrack(Context context, String token, String emoji, Bitmap emojiBitmap, boolean isPublic, boolean isCollaborative, boolean isWorldWide, String trackUri) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        emojiBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        Intent intent = new Intent(context, SmojifyService.class);
        intent.setAction(ACTION_REACT_TO_TRACK);
        intent.putExtra(EXTRA_EMOJI, emoji);
        intent.putExtra(EXTRA_TRACK_URI, trackUri);
        intent.putExtra(EXTRA_SPOTIFY_WEB_TOKEN, token);
        intent.putExtra(EXTRA_IS_PUBLIC, isPublic);
        intent.putExtra(EXTRA_IS_COLLABORATIVE, isCollaborative);
        intent.putExtra(EXTRA_IS_WORLD_WIDE, isWorldWide);
        intent.putExtra(EXTRA_EMOJI_BITMAP, byteArray);  // change this
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
                final boolean isPublic = intent.getBooleanExtra(EXTRA_IS_PUBLIC, false);
                final boolean isCollaborative = intent.getBooleanExtra(EXTRA_IS_COLLABORATIVE, false);
                final boolean isWorldWide = intent.getBooleanExtra(EXTRA_IS_WORLD_WIDE, false);

                // Convert byte array back into Bitmap
                byte[] byteArray = intent.getByteArrayExtra(EXTRA_EMOJI_BITMAP);
                Bitmap emojiBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                handleReactToTrack(token, emoji, emojiBitmap, isPublic, isCollaborative, isWorldWide, trackUri);
            }
        }
    }

    private void handleReactToTrack(String webToken, String emoji,Bitmap cover,boolean isPublic,boolean isCollaborative,boolean isWorldWide,String trackUri) {
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
                            spotifyAPI.updatePlaylistState(webToken, emojiName, cover, isPublic, isCollaborative, isWorldWide, trackUri, true);
                    //spotifyAPI.updateTrackInPlaylist(false, trackUri, playlistUri, webToken);
                } else {
                    // Handle error or no data case
                    Log.d("Emoji Name", "Failed to fetch emoji name");
                }
            }
        });
    }
}
