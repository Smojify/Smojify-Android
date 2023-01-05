package com.example.smojifypropre

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import androidx.core.content.ContentProviderCompat.requireContext
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import java.security.AccessController.getContext

/**
 * Implementation of App Widget functionality.
 */
public var spotifyAppRemote: SpotifyAppRemote? = null
public var pkg_name:String = ""

class HomeScreen : AppWidgetProvider() {
    companion object{
        public var awi:IntArray? = null
        public var awm:AppWidgetManager? = null
    }
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        awi = appWidgetIds
        awm = appWidgetManager
        pkg_name = context.packageName
        val views = RemoteViews(context.packageName, R.layout.home_screen)
        val clientId = "ed0e36f24a834fcd8fb4b0d0658fdb19"
        val redirectUri = "http://localhost:8888/callback"
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()
        if (checkNotNull(SpotifyAppRemote.isSpotifyInstalled(context))) {
            val intent = Intent(context, MyService::class.java)
            // intent.putExtra("ctx", Bundle().putParcelable("ctx", context))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        else
        {
            Log.e("ALLO", "HELLO")
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        when (intent?.action)
        {
            "PLAY" -> play(context!!)
            "PAUSE" -> pause(context!!)
            "PREV" -> spotifyAppRemote?.playerApi?.skipPrevious()
            "NEXT" -> spotifyAppRemote?.playerApi?.skipNext()
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.home_screen)
    //views.setTextViewText(R.id.appwidget_text, widgetText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun setOnlickButton(context: Context, javaClass: Class<HomeScreen>, action: String, layoutid: Int, awm: AppWidgetManager, ids: IntArray, views: RemoteViews)
{
    Log.i("set onclick", "")
    val intent = Intent(context, javaClass)
    intent.action = action
    views.setOnClickPendingIntent(layoutid, PendingIntent.getBroadcast(context, 0, intent, 0))
    awm.updateAppWidget(ids, views)
}

public fun play(context: Context)
{
    //Log.i("PKGNAME", pkg_name)
    val views = RemoteViews(context.packageName, R.layout.home_screen)
    views.setViewVisibility(R.id.btnPause, View.VISIBLE)
    views.setViewVisibility(R.id.btnPlay, View.INVISIBLE)
    AppWidgetManager.getInstance(context).updateAppWidget(AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, HomeScreen::class.java))[0], views)
    spotifyAppRemote?.playerApi?.resume()
}

public fun pause(context: Context)
{
    val views = RemoteViews(context.packageName, R.layout.home_screen)
    views.setViewVisibility(R.id.btnPlay, View.VISIBLE)
    views.setViewVisibility(R.id.btnPause, View.INVISIBLE)
    AppWidgetManager.getInstance(context).updateAppWidget(AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, HomeScreen::class.java))[0], views)
    spotifyAppRemote?.playerApi?.pause()
}