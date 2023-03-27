package com.smojify.smojify

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat
import com.google.android.material.internal.ContextUtils.getActivity
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.LoginActivity.REQUEST_CODE
import java.util.regex.Pattern


public var spotify_webtoken = ""
public var current_trackid = ""
public var trackTitle = ""
public var trackArtist = ""
fun getSpotify_webtoken(activity:Activity)
{
    val clientId = "d12d52e12c1e4b8fb0edaa8b8d7c2cec"
    val redirectUri = "http://localhost:8888/callback"
    var builder = AuthorizationRequest.Builder(clientId, AuthorizationResponse.Type.TOKEN, redirectUri)
    builder.setScopes(arrayOf<String>("playlist-read-private","playlist-read-collaborative","playlist-modify-private", "playlist-modify-public", "ugc-image-upload"))
    var request = builder.build()
    AuthorizationClient.openLoginActivity(activity, 0x10, request);
}

class MainActivity : AppCompatActivity() {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 0x10) {
            val response = AuthorizationClient.getResponse(resultCode, data)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> { spotify_webtoken = response.accessToken
                   // Log.e("", get_userid())
                    Log.e("SPOTIFY WEBTOKEN", spotify_webtoken) }
                AuthorizationResponse.Type.ERROR -> {Log.e("SPOTIFY WEBTOKEN", "ERROR")}
                else -> {Log.e("SPOTIFY WEBTOKEN", "else")}
            }
            Log.e("OnActivityResult", "CIAO")
            finish()
        }

    }
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var button:View = this.findViewById(R.id.button)
        val config = BundledEmojiCompatConfig(this)

        val inputText = findViewById<EditText>(R.id.emoji_input)

        lateinit var reacting_emoji: TextView // Declare the view


        inputText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Not needed for this implementation
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this implementation
            }

            var isUpdating = false // Add this flag at the top of your class

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    val text = it.toString()
                    if (!text.isBlank() && !isUpdating) { // Check if the flag is false before proceeding
                        isUpdating = true // Set the flag to true
                        val reacting_emoji = findViewById<TextView>(R.id.reacting_emoji)
                        val emoji = inputText.text.toString()
                        reacting_emoji.text = emoji
                        reactToTrack(reacting_emoji, emoji)
                        inputText.text.clear()
                        isUpdating = false // Set the flag back to false
                    }
                }
            }


        })


        val acti:Activity = this
        EmojiCompat.init(config).registerInitCallback(object: EmojiCompat.InitCallback() {
            override fun onInitialized() {
                button.setOnClickListener {
                    val remoteviews =
                        RemoteViews(applicationContext.packageName, R.layout.home_screen)
                    val intent = Intent(applicationContext, HomeScreen::class.java)
                    intent.action = "NONE"
                    remoteviews.setOnClickPendingIntent(
                        R.id.homescreen_layout,
                        PendingIntent.getActivity(
                            applicationContext,
                            0,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                    val appwidgetman = AppWidgetManager.getInstance(applicationContext)
                    val appwidgetids = appwidgetman.getAppWidgetIds(
                        ComponentName(
                            applicationContext,
                            HomeScreen::class.java
                        )
                    )
                    appwidgetman.updateAppWidget(appwidgetids[0], remoteviews)
                    getSpotify_webtoken(acti)
                    init_spot(acti)
                }
            }
        })
    }
}
