package com.smojify.smojify

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.View
import androidx.core.view.drawToBitmap
import kotlinx.coroutines.*
import org.jsoup.Jsoup

fun init_spot(context: Context)
{
    val intent = Intent(context, MyService::class.java)
    intent.action = "init_spot"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        context.startForegroundService(intent)
    else
        context.startService(intent)
}
fun getEmojiName(emoji: String): String? {
    var title: String? = null
    runBlocking {
        withContext(Dispatchers.IO) {
            try {
                val url = "https://emojipedia.org/$emoji/"
                val doc = Jsoup.connect(url).get()
                title = doc.select("h1").text().removeSuffix(" emoji")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    return title
}

fun loadBitmapFromView(v: View): Bitmap? {
    val b =
        Bitmap.createBitmap(40,40, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    v.layout(v.left, v.top, v.right, v.bottom)
    v.drawToBitmap(Bitmap.Config.ARGB_8888)
    v.draw(c)
    return v.drawToBitmap(Bitmap.Config.ARGB_8888)
}
