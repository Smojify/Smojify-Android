package com.smojify.smojify

import android.annotation.SuppressLint
import android.app.*
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContextCompat.getSystemService
import androidx.emoji2.text.EmojiCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.material.internal.ContextUtils
import com.google.android.material.internal.ContextUtils.getActivity
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.SpotifyAppRemote.disconnect
import com.spotify.android.appremote.api.error.SpotifyConnectionTerminatedException
import com.spotify.protocol.types.Image
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse


class MyService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        service_launched = true
        val CHANNEL_ID = "channelID"
        val CHANNEL_NAME = "channelName"
        val NOTIF_ID = 0
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, HomeScreen::class.java)
        intent.action = "NONE"
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        var notifView = RemoteViews(applicationContext.packageName, R.layout.notif)
        lateinit var builder: Notification.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            manager.createNotificationChannel(notificationChannel)

             builder = Notification.Builder(this, CHANNEL_ID)
                .setContent(notifView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
                 .setOngoing(true)
        } else {

            builder = Notification.Builder(this)
                .setContent(notifView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
        }
        //notifView.setTextViewText(R.id.notif_dialog_btn, Html.fromHtml(EmojiCompat.get().process("&#x2795;").toString()))
        val dialog_intent = Intent(applicationContext, dialogactivity::class.java)
        dialog_intent.action = "notif_opendialog"
        notifView.setOnClickPendingIntent(R.id.notif_dialog_btn, PendingIntent.getActivity(applicationContext, 0, dialog_intent, PendingIntent.FLAG_UPDATE_CURRENT))
        AppWidgetManager.getInstance(applicationContext).updateAppWidget(notifView.layoutId, notifView)
        var notification = builder.build()
        val notif_intent = Intent(applicationContext, StackRemoteViewsFactory::class.java)



        //super.onCreate()
        val appWidgetManager = AppWidgetManager.getInstance(this.applicationContext)
        val appWidgetIds = AppWidgetManager.getInstance(this.applicationContext).getAppWidgetIds(ComponentName(this.applicationContext, HomeScreen::class.java))
        val notifids = AppWidgetManager.getInstance(this.applicationContext).getAppWidgetIds(ComponentName(this.applicationContext, StackRemoteViewsFactory::class.java))
        val clientId = "ed0e36f24a834fcd8fb4b0d0658fdb19"
        val redirectUri = "http://localhost:8888/callback"
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()
        val views = RemoteViews(this.packageName, R.layout.home_screen)
        val context = this.applicationContext
        views.setOnClickPendingIntent(R.id.homescreen_layout, pendingIntent)
        //appWidgetManager.updateAppWidget(R.id.notific, RemoteViews(this.packageName, R.layout.notif))
        startForeground(1234, notification)
        manager.notify(1234, notification)
        SpotifyAppRemote.connect(
            this,
            connectionParams,
            object : Connector.ConnectionListener {
                @SuppressLint("RestrictedApi")
                override fun onConnected(appRemote: SpotifyAppRemote) {
                    spotifyAppRemote = appRemote
                    setOnlickButton(context, HomeScreen::class.java,"PLAY", R.id.btnPlay, appWidgetManager, appWidgetIds, views)
                    setOnlickButton(context, HomeScreen::class.java,"PAUSE", R.id.btnPause, appWidgetManager, appWidgetIds, views)
                    setOnlickButton(context, HomeScreen::class.java,"PREV", R.id.btnPrev, appWidgetManager, appWidgetIds, views)
                    setOnlickButton(context, HomeScreen::class.java, "NEXT", R.id.btnNext, appWidgetManager, appWidgetIds, views)
                    Log.i("Service", "SPOTIFY CONNECTED =D")
                    appRemote.playerApi.subscribeToPlayerState().setEventCallback()
                    {
                        current_trackid = it.track.uri
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
                        trackTitle = it.track.name
                        trackArtist = it.track.artist.name
                        views.setTextViewText(R.id.trackTitle, it.track.name)
                        views.setTextViewText(R.id.trackArtist, it.track.artist.name)
                        appRemote.imagesApi.getImage(it.track.imageUri, Image.Dimension.THUMBNAIL).setResultCallback { bmp ->
                            views.setImageViewBitmap(R.id.imageView, bmp)
                            appWidgetManager.updateAppWidget( appWidgetIds[0], views)
                        }
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    if(throwable is SpotifyConnectionTerminatedException){
                        SpotifyAppRemote.connect(context, connectionParams, this)
                    }
                    Log.e("MainActivity", throwable.message, throwable)
                    // Something went wrong when attempting to connect! Handle errors here
                }
            })

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals("STOP"))
        {
            Log.i("STOP", "*****STOP*******")
            SpotifyAppRemote.disconnect(spotifyAppRemote!!)
            stopForeground(true);
            stopSelfResult(startId);
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

class StackRemoteViewsFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {

    private lateinit var widgetItems: List<String>

    override fun onCreate() {
        Log.i("GRIDVIEW", "**ONCREATE**")
        widgetItems = listOf<String>(
            "&#x1F600;", "&#x1F603;", "&#x1F604;", "&#x1F601;", "&#x1F606;", "&#x1F605;", "&#x1F923;", "&#x1F602;", "&#x1F642;", "&#x1F643;", "&#x1FAE0;", "&#x1F609;", "&#x1F60A;", "&#x1F607;", "&#x1F970;", "&#x1F60D;", "&#x1F929;", "&#x1F618;", "&#x1F617;", "&#x263A;", "&#x263A;", "&#x1F61A;", "&#x1F619;", "&#x1F972;", "&#x1F60B;", "&#x1F61B;", "&#x1F61C;", "&#x1F92A;", "&#x1F61D;", "&#x1F911;", "&#x1F917;", "&#x1F92D;", "&#x1FAE2;", "&#x1FAE3;", "&#x1F92B;", "&#x1F914;", "&#x1FAE1;", "&#x1F910;", "&#x1F928;", "&#x1F610;", "&#x1F611;", "&#x1F636;", "&#x1FAE5;", "&#x1F636;", "&#x1F636;", "&#x1F60F;", "&#x1F612;", "&#x1F644;", "&#x1F62C;", "&#x1F62E;", "&#x1F925;", "&#x1FAE8;", "&#x1F60C;", "&#x1F614;", "&#x1F62A;", "&#x1F924;", "&#x1F634;", "&#x1F637;", "&#x1F912;", "&#x1F915;", "&#x1F922;", "&#x1F92E;", "&#x1F927;", "&#x1F975;", "&#x1F976;", "&#x1F974;", "&#x1F635;", "&#x1F635;", "&#x1F92F;", "&#x1F920;", "&#x1F973;", "&#x1F978;", "&#x1F60E;", "&#x1F913;", "&#x1F9D0;", "&#x1F615;", "&#x1FAE4;", "&#x1F61F;", "&#x1F641;", "&#x2639;", "&#x2639;", "&#x1F62E;", "&#x1F62F;", "&#x1F632;", "&#x1F633;", "&#x1F97A;", "&#x1F979;", "&#x1F626;", "&#x1F627;", "&#x1F628;", "&#x1F630;", "&#x1F625;", "&#x1F622;", "&#x1F62D;", "&#x1F631;", "&#x1F616;", "&#x1F623;", "&#x1F61E;", "&#x1F613;", "&#x1F629;", "&#x1F62B;", "&#x1F971;", "&#x1F624;", "&#x1F621;", "&#x1F620;", "&#x1F92C;", "&#x1F608;", "&#x1F47F;", "&#x1F480;", "&#x2620;", "&#x2620;", "&#x1F4A9;", "&#x1F921;", "&#x1F479;", "&#x1F47A;", "&#x1F47B;", "&#x1F47D;", "&#x1F47E;", "&#x1F916;", "&#x1F63A;", "&#x1F638;", "&#x1F639;", "&#x1F63B;", "&#x1F63C;", "&#x1F63D;", "&#x1F640;", "&#x1F63F;", "&#x1F63E;", "&#x1F648;", "&#x1F649;", "&#x1F64A;", "&#x1F48C;", "&#x1F498;", "&#x1F49D;", "&#x1F496;", "&#x1F497;", "&#x1F493;", "&#x1F49E;", "&#x1F495;", "&#x1F49F;", "&#x2763;", "&#x2763;", "&#x1F494;", "&#x2764;", "&#x2764;", "&#x2764;", "&#x2764;", "&#x2764;", "&#x2764;", "&#x1FA77;", "&#x1F9E1;", "&#x1F49B;", "&#x1F49A;", "&#x1F499;", "&#x1FA75;", "&#x1F49C;", "&#x1F90E;", "&#x1F5A4;", "&#x1FA76;", "&#x1F90D;", "&#x1F48B;", "&#x1F4AF;", "&#x1F4A2;", "&#x1F4A5;", "&#x1F4AB;", "&#x1F4A6;", "&#x1F4A8;", "&#x1F573;", "&#x1F573;", "&#x1F4AC;", "&#x1F441;", "&#x1F441;", "&#x1F441;", "&#x1F441;", "&#x1F5E8;", "&#x1F5E8;", "&#x1F5EF;", "&#x1F5EF;", "&#x1F4AD;", "&#x1F4A4;"
        )
    }

    override fun onDataSetChanged() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }

    override fun getCount(): Int {
        TODO("Not yet implemented")
    }

    @SuppressLint("RemoteViewLayout")
    override fun getViewAt(p0: Int): RemoteViews {
        Log.i("GRIDVIEW", "*****GRIDVIEW******")
        return RemoteViews(context.packageName, R.layout.gridview_item).apply {
            setTextViewText(R.id.emoji, widgetItems[p0])
        }
    }

    override fun getLoadingView(): RemoteViews {
        TODO("Not yet implemented")
    }

    override fun getViewTypeCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItemId(p0: Int): Long {
        TODO("Not yet implemented")
    }

    override fun hasStableIds(): Boolean {
        TODO("Not yet implemented")
    }
}
