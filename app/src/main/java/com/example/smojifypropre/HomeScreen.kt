package com.example.smojifypropre

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat
import com.spotify.android.appremote.api.SpotifyAppRemote


/**
 * Implementation of App Widget functionality.
 */
public var spotifyAppRemote: SpotifyAppRemote? = null
public var pkg_name:String = ""
public var service_launched:Boolean = false

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
        if (checkNotNull(SpotifyAppRemote.isSpotifyInstalled(context))) {
            var emo = RemoteViews(context.packageName, R.layout.gridview_item)
            val config = BundledEmojiCompatConfig(context)
            EmojiCompat.init(config).registerInitCallback(object: EmojiCompat.InitCallback() {
                override fun onInitialized() {
                    init_emojibar(context, views, appWidgetIds)
                    views.setViewVisibility(R.id.btnPause, View.INVISIBLE)
                    val main_intent = Intent(context, MainActivity::class.java)
                    views.setOnClickPendingIntent(R.id.homescreen_layout, PendingIntent.getActivity(context, 0, main_intent, PendingIntent.FLAG_UPDATE_CURRENT))
                    appWidgetManager.updateAppWidget(appWidgetIds[0], views)
                }
            })
            //appWidgetManager.updateAppWidget(emo.layoutId, emo)
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
        if (service_launched) {
            var stop_intent = Intent(context, MyService::class.java)
            stop_intent.action = "STOP"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context?.startForegroundService(stop_intent)
            else
                context?.startService(stop_intent)
            SpotifyAppRemote.disconnect(spotifyAppRemote)
        }
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        when (intent?.action)
        {
            "INIT" -> init_spot(context!!)
            "PLAY" -> play(context!!)
            "PAUSE" -> pause(context!!)
            "PREV" -> spotifyAppRemote?.playerApi?.skipPrevious()
            "NEXT" -> spotifyAppRemote?.playerApi?.skipNext()
        }
    }
}

internal fun init_emojibar(context: Context, views: RemoteViews, appWidgetIds: IntArray)
{
    var listo1:List<String> = listOf<String>(
        "&#x1F600;", "&#x1F603;", "&#x1F604;", "&#x1F601;", "&#x1F606;", "&#x1F605;", "&#x1F923;", "&#x1F602;", "&#x1F642;", "&#x1F643;", "&#x1FAE0;", "&#x1F609;", "&#x1F60A;", "&#x1F607;", "&#x1F970;", "&#x1F60D;", "&#x1F929;", "&#x1F618;", "&#x1F617;", "&#x263A;", "&#x263A;", "&#x1F61A;", "&#x1F619;", "&#x1F972;", "&#x1F60B;", "&#x1F61B;", "&#x1F61C;", "&#x1F92A;", "&#x1F61D;", "&#x1F911;", "&#x1F917;", "&#x1F92D;", "&#x1FAE2;", "&#x1FAE3;", "&#x1F92B;", "&#x1F914;", "&#x1FAE1;", "&#x1F910;", "&#x1F928;", "&#x1F610;", "&#x1F611;", "&#x1F636;", "&#x1FAE5;", "&#x1F636;", "&#x1F636;", "&#x1F60F;", "&#x1F612;", "&#x1F644;", "&#x1F62C;", "&#x1F62E;", "&#x1F925;", "&#x1FAE8;", "&#x1F60C;", "&#x1F614;", "&#x1F62A;", "&#x1F924;", "&#x1F634;", "&#x1F637;", "&#x1F912;", "&#x1F915;", "&#x1F922;", "&#x1F92E;", "&#x1F927;", "&#x1F975;", "&#x1F976;", "&#x1F974;", "&#x1F635;", "&#x1F635;", "&#x1F92F;", "&#x1F920;", "&#x1F973;", "&#x1F978;", "&#x1F60E;", "&#x1F913;", "&#x1F9D0;", "&#x1F615;", "&#x1FAE4;", "&#x1F61F;", "&#x1F641;", "&#x2639;", "&#x2639;", "&#x1F62E;", "&#x1F62F;", "&#x1F632;", "&#x1F633;", "&#x1F97A;", "&#x1F979;", "&#x1F626;", "&#x1F627;", "&#x1F628;", "&#x1F630;", "&#x1F625;", "&#x1F622;", "&#x1F62D;", "&#x1F631;", "&#x1F616;", "&#x1F623;", "&#x1F61E;", "&#x1F613;", "&#x1F629;", "&#x1F62B;", "&#x1F971;", "&#x1F624;", "&#x1F621;", "&#x1F620;", "&#x1F92C;", "&#x1F608;", "&#x1F47F;", "&#x1F480;", "&#x2620;", "&#x2620;", "&#x1F4A9;", "&#x1F921;", "&#x1F479;", "&#x1F47A;", "&#x1F47B;", "&#x1F47D;", "&#x1F47E;", "&#x1F916;", "&#x1F63A;", "&#x1F638;", "&#x1F639;", "&#x1F63B;", "&#x1F63C;", "&#x1F63D;", "&#x1F640;", "&#x1F63F;", "&#x1F63E;", "&#x1F648;", "&#x1F649;", "&#x1F64A;", "&#x1F48C;", "&#x1F498;", "&#x1F49D;", "&#x1F496;", "&#x1F497;", "&#x1F493;", "&#x1F49E;", "&#x1F495;", "&#x1F49F;", "&#x2763;", "&#x2763;", "&#x1F494;", "&#x2764;", "&#x2764;", "&#x2764;", "&#x2764;", "&#x2764;", "&#x2764;", "&#x1FA77;", "&#x1F9E1;", "&#x1F49B;", "&#x1F49A;", "&#x1F499;", "&#x1FA75;", "&#x1F49C;", "&#x1F90E;", "&#x1F5A4;", "&#x1FA76;", "&#x1F90D;", "&#x1F48B;", "&#x1F4AF;", "&#x1F4A2;", "&#x1F4A5;", "&#x1F4AB;", "&#x1F4A6;", "&#x1F4A8;", "&#x1F573;", "&#x1F573;", "&#x1F4AC;", "&#x1F441;", "&#x1F441;", "&#x1F441;", "&#x1F441;", "&#x1F5E8;", "&#x1F5E8;", "&#x1F5EF;", "&#x1F5EF;", "&#x1F4AD;", "&#x1F4A4;"
    )
    //var emo = RemoteViews(context.packageName, R.layout.gridview_item)
    //val views = RemoteViews(context.packageName, R.layout.home_screen)
    //views.setTextViewText(R.id.dialog_btn, Html.fromHtml(EmojiCompat.get().process("&#x2795;").toString()))
    val intent = Intent(context, dialogactivity::class.java)
    intent.action = "opendialog"
    views.setOnClickPendingIntent(R.id.dialog_btn, PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
    AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds[0], views)
    //views.addView(R.id.emoji_bar, views)
    /*emo = RemoteViews(context.packageName, R.layout.gridview_item)
    emo.setTextViewText(R.id.emoji, Html.fromHtml(EmojiCompat.get().process(listo1[0]).toString()))
    views.addView(R.id.emoji_bar, emo)
    emo.setTextViewText(R.id.emoji, Html.fromHtml(EmojiCompat.get().process(listo1[1]).toString()))
    views.addView(R.id.emoji_bar, emo)
    emo.setTextViewText(R.id.emoji, Html.fromHtml(EmojiCompat.get().process(listo1[2]).toString()))
    views.addView(R.id.emoji_bar, emo)
    emo.setTextViewText(R.id.emoji, Html.fromHtml(EmojiCompat.get().process(listo1[3]).toString()))
    views.addView(R.id.emoji_bar, emo)*/
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
    views.setOnClickPendingIntent(layoutid, PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE))
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