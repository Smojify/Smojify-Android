package com.smojify.smojify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.protocol.client.CallResult;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class PlayerActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final String CLIENT_ID = "d12d52e12c1e4b8fb0edaa8b8d7c2cec";
    private static String currentTrackUri = "";
    private SpotifyAppRemote mSpotifyAppRemote;
    private EmojiUtil emojiManager;
    String playlistName;
    SpotifyUtil spotify;
    private SmojifyService smojifyService;
    private String spotifyWebToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        emojiManager = new EmojiUtil();
        smojifyService = new SmojifyService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[] {
                "streaming",
                "playlist-read-private",
                "playlist-read-collaborative",
                "playlist-modify-private",
                "playlist-modify-public",
                "ugc-image-upload"
        });

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
                        currentTrackUri = track.uri;
                        updatePlayerState(track);
                    }
                });

        EditText reactingEmojis = findViewById(R.id.emojiInput);
        ImageButton logoButton = findViewById(R.id.logoButton);
        Button emojiButton = findViewById(R.id.emojiButton);
        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    reactingEmojis.setFocusableInTouchMode(true);
                    reactingEmojis.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(reactingEmojis, InputMethodManager.SHOW_FORCED);
            }
        });
        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reactingEmojis.setFocusableInTouchMode(true);
                reactingEmojis.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(reactingEmojis, InputMethodManager.SHOW_FORCED);
            }
        });



        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called when the text is being changed
                // Add items to the queue based on the input text
                String inputText = s.toString();
                if (TextUtils.isEmpty(inputText.trim())) {
                    return;
                }
                Log.e("", "Update Text: " + inputText);
                reactingEmojis.getText().clear();
                Log.e("", "User Reaction: " + inputText);
                drawEmoji(inputText);

                // Get the bitmap from the emojiButton
                Button emojiButton = findViewById(R.id.emojiButton);
                Bitmap emojiBitmap = getBitmapFromView(emojiButton);

                // Send the bitmap to the SmojifyService
                smojifyService.reactToTrack(getApplicationContext(), inputText, emojiBitmap, currentTrackUri, spotifyWebToken);
            }


            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text has been changed
                // Process the new text as needed
            }
        };

// Add the TextWatcher to the EditText
        reactingEmojis.addTextChangedListener(textWatcher);

    }

    private void drawEmoji(String inputText) {
        Log.e("", "Draw emoji");
        ImageButton logo = findViewById(R.id.logoButton);
        Button emoji = findViewById(R.id.emojiButton);
        logo.setVisibility(View.GONE);
        emoji.setVisibility(View.VISIBLE);
        emoji.setText("");
        emoji.setText(inputText);
    }

    private void updatePlayerState(Track track) {
        TextView songTitle = findViewById(R.id.songTitleTextView);
        TextView artistName = findViewById(R.id.artistTextView);
        TextView playlistName = findViewById(R.id.playlistTextView);
        ImageView cover = findViewById(R.id.albumCoverImageView);
        currentTrackUri = track.uri;

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
                    if (response.getError() == null) {
                    spotifyWebToken = response.getAccessToken();
                    Log.d("Spotify Web Token", spotifyWebToken);
                    smojifyService.startService(this);
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
                                    spotify = new SpotifyUtil();
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
                    } else {
                        Log.e("Spotify Web Token", "Error obtaining token: " + response.getError());
                    }
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
    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}