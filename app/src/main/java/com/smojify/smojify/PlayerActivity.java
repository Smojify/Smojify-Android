package com.smojify.smojify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.protocol.client.CallResult;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.net.URI;

public class PlayerActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final String CLIENT_ID = "d12d52e12c1e4b8fb0edaa8b8d7c2cec";
    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // We will start writing our code here.
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String []{
                "streaming",
                "playlist-read-private",
                "playlist-read-collaborative",
                "playlist-modify-private",
                "playlist-modify-public",
                "ugc-image-upload"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    protected void connected() {
        // Then we will write some more code here.
        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        updatePlayerState(track);
                    }
                });
    }

    private void updatePlayerState(Track track) {
        TextView songTitle = findViewById(R.id.songTitleTextView);
        TextView artistName = findViewById(R.id.artistTextView);
        ImageView cover = findViewById(R.id.albumCoverImageView);

        songTitle.setText(track.name);
        artistName.setText(track.artist.name);
        mSpotifyAppRemote.getImagesApi().getImage(
                track.imageUri
        ).setResultCallback(
                new CallResult.ResultCallback<Bitmap>() {
                    @Override public void onResult(Bitmap bitmap) {
                        cover.setImageBitmap(bitmap);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Aaand we will finish off here.
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    Log.e("Spotify Web Token", "OK");
                    // Set the connection parameters
                    ConnectionParams connectionParams =
                            new ConnectionParams.Builder(CLIENT_ID)
                                    .setRedirectUri(REDIRECT_URI)
                                    .showAuthView(true)
                                    .build();
                    SpotifyAppRemote.connect(this, connectionParams,
                            new Connector.ConnectionListener() {

                                @Override
                                public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                                    mSpotifyAppRemote = spotifyAppRemote;
                                    Log.d("MainActivity", "Connected! Yay!");

                                    // Now you can start interacting with App Remote
                                    connected();
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    Log.e("MainActivity", throwable.getMessage(), throwable);

                                    // Something went wrong when attempting to connect! Handle errors here
                                }
                            });
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    Log.e("Spotify Web Token", "KO");
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
                    Log.e("Spotify Web Token", "Undefined behavior");
            }
        }
    }
}