package com.example.smojifypropre

import android.app.*
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Image
import java.nio.file.attribute.AclEntry.Builder


class MyService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        val CHANNEL_ID = "channelID"
        val CHANNEL_NAME = "channelName"
        val NOTIF_ID = 0
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, HomeScreen::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        lateinit var builder: Notification.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            manager.createNotificationChannel(notificationChannel)

             builder = Notification.Builder(this, CHANNEL_ID)
                .setContent(RemoteViews(this.packageName, R.layout.notif))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
                 .setOngoing(true)
        } else {

            builder = Notification.Builder(this)
                .setContent(RemoteViews(this.packageName, R.layout.notif))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
        }
        var notification = builder.build()
        startForeground(1234, notification)
        manager.notify(1234, notification)
        //super.onCreate()
        val appWidgetManager = AppWidgetManager.getInstance(this.applicationContext)
        val appWidgetIds = AppWidgetManager.getInstance(this.applicationContext).getAppWidgetIds(ComponentName(this.applicationContext, HomeScreen::class.java))
        val clientId = "ed0e36f24a834fcd8fb4b0d0658fdb19"
        val redirectUri = "http://localhost:8888/callback"
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()
        val views = RemoteViews(this.packageName, R.layout.home_screen)
        val context = this.applicationContext
        SpotifyAppRemote.connect(
            this,
            connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(appRemote: SpotifyAppRemote) {
                    spotifyAppRemote = appRemote
                    setOnlickButton(context, HomeScreen::class.java,"PLAY", R.id.btnPlay, appWidgetManager, appWidgetIds, views)
                    setOnlickButton(context, HomeScreen::class.java,"PAUSE", R.id.btnPause, appWidgetManager, appWidgetIds, views)
                    setOnlickButton(context, HomeScreen::class.java,"PREV", R.id.btnPrev, appWidgetManager, appWidgetIds, views)
                    setOnlickButton(context, HomeScreen::class.java, "NEXT", R.id.btnNext, appWidgetManager, appWidgetIds, views)
                    Log.i("Service", "SPOTIFY CONNECTED =D")
                    appRemote.playerApi.subscribeToPlayerState().setEventCallback()
                    {
                        if (it.isPaused)
                        {
                            views.setViewVisibility(R.id.btnPlay, View.VISIBLE)
                            views.setViewVisibility(R.id.btnPause, View.INVISIBLE)
                        }
                        else
                        {
                            views.setViewVisibility(R.id.btnPlay, View.INVISIBLE)
                            views.setViewVisibility(R.id.btnPause, View.VISIBLE)
                        }
                        views.setTextViewText(R.id.trackTitle, it.track.name)
                        views.setTextViewText(R.id.trackArtist, it.track.artist.name)
                        appRemote.imagesApi.getImage(it.track.imageUri, Image.Dimension.THUMBNAIL).setResultCallback { bmp ->
                            views.setImageViewBitmap(R.id.imageView, bmp)
                            appWidgetManager.updateAppWidget( appWidgetIds[0], views)
                        }
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e("MainActivity", throwable.message, throwable)
                    // Something went wrong when attempting to connect! Handle errors here
                }
            })

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
      //  disconnect(spotifyAppRemote)
    }
}