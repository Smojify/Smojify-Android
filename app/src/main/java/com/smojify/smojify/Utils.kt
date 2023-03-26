package com.smojify.smojify

import android.content.Context
import android.content.Intent
import android.os.Build
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
