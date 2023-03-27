package com.smojify.smojify

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.emoji2.text.EmojiCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.util.*
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL



var tabLayout: TabLayout? = null

var actictx:Context? = null

class dialogactivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialogactivity)
        actictx = this
        tabLayout = findViewById<TabLayout>(R.id.tablayout)
        var viewPager: ViewPager2? = findViewById<ViewPager2?>(R.id.viewpager)
        // Create an adapter that knows which fragment should be shown on each page
        val adapter2 = SimpleFragmentPagerAdapter(this)
        // Set the adapter onto the view pager
        viewPager?.adapter = adapter2
        viewPager?.registerOnPageChangeCallback(viewpagerchange())
        // viewPager.addOnLayoutChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout?.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
               viewPager?.setCurrentItem(tab.position, true)
                adapter2.notifyDataSetChanged()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }
}

class viewpagerchange: ViewPager2.OnPageChangeCallback()
{
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
    }

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        tabLayout?.selectTab(tabLayout?.getTabAt(position), true)
       // Log.e("Selected_Page", position.toString())
    }

    override fun onPageScrollStateChanged(state: Int) {
        super.onPageScrollStateChanged(state)
    }
}

public class GridRVAdapter(
    // on below line we are creating two
    // variables for course list and context
    private val courseList: List<String>,
    private val context: Context
) :
    BaseAdapter() {
    // in base adapter class we are creating variables
    // for layout inflater, course image view and course text view.
    private var layoutInflater: LayoutInflater? = null
    private lateinit var courseIV: TextView

    // below method is use to return the count of course list
    override fun getCount(): Int {
        return courseList.size
    }

    // below function is use to return the item of grid view.
    override fun getItem(position: Int): Any? {
        return null
    }

    // below function is use to return item id of grid view.
    override fun getItemId(position: Int): Long {
        return 0
    }

    // in below function we are getting individual item of grid view.
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        // on blow line we are checking if layout inflater
        // is null, if it is null we are initializing it.
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        // on the below line we are checking if convert view is null.
        // If it is null we are initializing it.
        if (convertView == null) {
            // on below line we are passing the layout file
            // which we have to inflate for each item of grid view.
            convertView = layoutInflater!!.inflate(R.layout.gridview_item, null)
        }
        // on below line we are initializing our course image view
        // and course text view with their ids.
        courseIV = convertView!!.findViewById(R.id.emoji)
        // on below line we are setting image for our course image view ++
        val unicode = 0x1F600
        courseIV.text = Html.fromHtml(EmojiCompat.get().process(courseList[position]).toString())
        courseIV.setOnClickListener{
            val dialog_activity = context as Activity
            LoadingScreen.displayLoadingWithText(actictx, "Adding " + trackArtist + " - " + trackTitle + " to " + it.findViewById<TextView>(R.id.emoji).text, false, convertView)
        }
               // LoadingScreen.hideLoading()
        return convertView
    }
}

fun item_clicked(view: View)
{
    view.findViewById<TextView>(R.id.emoji).setBackgroundColor(Color.GREEN)
}

class SimpleFragmentPagerAdapter(fm: FragmentActivity?) : FragmentStateAdapter(fm!!) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                Emojis1()
            }
            1 -> {
                Emojis2()
            }
            2 -> {
                Emojis3()
            }
            3 -> {
                emoji4()
            }
            4 -> {
                Emojis5()
            }
            5 -> {
                Emojis6()
            }
            6 -> {
                Emojis7()
            }
            7 -> {
                Emojis8()
            }
            else -> {
                Emojis9()
            }
        }
    }

    override fun getItemCount(): Int {
        return 9
    }
}