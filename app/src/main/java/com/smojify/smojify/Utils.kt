package com.smojify.smojify

import android.content.Context
import android.content.Intent
import android.os.Build

fun init_spot(context: Context)
{
    val intent = Intent(context, MyService::class.java)
    intent.action = "init_spot"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        context.startForegroundService(intent)
    else
        context.startService(intent)
}
