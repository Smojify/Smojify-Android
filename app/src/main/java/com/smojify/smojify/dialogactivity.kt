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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream


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

fun loadBitmapFromView(v: View): Bitmap? {
    val b =
        Bitmap.createBitmap(40,40, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    v.layout(v.left, v.top, v.right, v.bottom)
    v.drawToBitmap(Bitmap.Config.ARGB_8888)
    v.draw(c)
    return v.drawToBitmap(Bitmap.Config.ARGB_8888)
}

data class explicit_content(val filter_enabled:Boolean, val filter_lock:Boolean)
data class external_urls(val spotify:String)
data class followers(val href:String, val total:Int)
data class images(val url:String, val height:Int, val width:Int)
data class spotify_userprofile(val country:String, val display_name:String, val email:String, val explicit:explicit_content, val externalUrls: external_urls, val followers:followers, val href:String, val id:String, val images:List<images>, val product:String, val type:String, val url:String)

fun get_userid(): String
{
    val client = HttpClient() {
        install(ContentNegotiation) {
            gson()
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.spotify.com"
                path("/v1/")
                //parameters.append("token", "abc123")
            }
            header("Authorization", "Bearer " + spotify_webtoken)
        }
    }
    var returno:String = ""
    runBlocking {
        val response: HttpResponse = client.get("me")
        val json = response.body<spotify_userprofile>()
        Log.e("ewew", json.id)
        returno =  json.id
    }
    return returno
}

data class playlist_builder(val name:String, val public:Boolean, val collaborative:Boolean, val description: String)
data class playlist_response(val collaborative: Boolean, val description: String, val externalUrls: external_urls, val followers: followers, val href: String, val id: String, val images:List<images>, val name: String)
data class playlists_list(val items:Array<playlist_response>)

fun add_to_playlist(view:View, track_uri:String, emoji:String)
{
    val playlist = getplaylist(view, emoji)
    val client = HttpClient() {
        install(ContentNegotiation) {
            gson()
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.spotify.com"
                path("/v1/playlists/" + playlist.id + "/")
                //parameters.append("token", "abc123")
            }
            header("Authorization", "Bearer " + spotify_webtoken)
        }
    }
    lateinit var playlists:playlists_list
    runBlocking {
        val response: HttpResponse = client.post("tracks"){
            parameter("position", 0)
            parameter("uris", track_uri)
        }
        Log.e("ADD_PLAYLIST",response.body())
        LoadingScreen.hideLoading()
        Toast.makeText(actictx, trackTitle + " by " + trackArtist + " added to " + emoji,Toast.LENGTH_SHORT).show()
        val json = response.body<playlists_list>()
    }
}

fun getplaylist(view:View, emoji: String):playlist_response
{
    val client = HttpClient() {
        install(ContentNegotiation) {
            gson()
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.spotify.com"
                path("/v1/me/")
                //parameters.append("token", "abc123")
            }
            header("Authorization", "Bearer " + spotify_webtoken)
        }
    }
    lateinit var playlists:playlists_list
    runBlocking {
        val response: HttpResponse = client.get("playlists"){
            parameter("limit", 50)
            parameter("offset", 0)
        }
        val json = response.body<playlists_list>()
        Log.e("Playlist name", response.body())
        playlists =  json
    }
    val playlist = playlists.items.find { it.name.equals(emoji) }
    if (playlist != null)
        return playlist
    return create_playlist(view, emoji)
}


fun create_playlist(view:View, emoji:String):playlist_response
{
    lateinit var respbody:playlist_response
    val client = HttpClient() {
        install(ContentNegotiation) {
            gson()
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.spotify.com"
                path("/v1/users/" + get_userid() + "/")

            }
            header("Authorization", "Bearer " + spotify_webtoken)
        }
    }
    Log.e("URI", "/v1/users/" + get_userid() + "/")
    runBlocking {
        val response: HttpResponse = client.post("playlists"){
            contentType(ContentType.Application.Json)
            setBody(playlist_builder(emoji, false, false, "Playlist created by Smojify"))
        }
       // getSpotify_webtoken(view)
        respbody = response.body()
        val client2 = HttpClient() {
            install(ContentNegotiation) {
                gson()
            }
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.spotify.com"
                    path("/v1/playlists/" + respbody.id + "/")

                }
                header("Authorization",  "Bearer " + spotify_webtoken)
            }
        }
        // Log.e("URI", "/v1/users/" + get_userid() + "/")
        runBlocking {
            val response2: HttpResponse = client2.put("images"){
                //accept(ContentType.Application.Json)
                //contentType(ContentType.Image.JPEG)
                val bitmap = loadBitmapFromView(view)
                val stream = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                val image = stream.toByteArray()
                var base64 = Base64.encodeToString(image, 0, image.size, 0)
                //base64 = base64
                setBody(base64.trim(' ', '\n').replace("\n",""))
                Log.e("BASE64", base64.trim(' ', '\n').replace("\n","") +"**")
            }
            Log.e("ewew", response2.body())
        }
        //val json = response.body<spotify_userprofile>()

    }
    return respbody
}

object LoadingScreen {
    var dialog: Dialog? = null //obj
    fun displayLoadingWithText(context: Context?, text: String?, cancelable: Boolean, convertView: View?) { // function -- context(parent (reference))
        dialog = Dialog(context!!)
        if (dialog == null)
            Log.e("LOADING SCREEN", "COULDNT CREATE DIALOG")
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.layout_loading_screen)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(cancelable)
        val textView = dialog!!.findViewById<TextView>(R.id.text)
        textView.text = text
        dialog!!.setOnShowListener { add_to_playlist(
            convertView!!,
            current_trackid,
            convertView!!.findViewById<TextView>(R.id.emoji).text.toString()
        ) }
        dialog!!.show()
    }

    fun hideLoading() {
        try {
            if (dialog != null) {
                dialog!!.dismiss()
                val acti = actictx as Activity
                acti.finish()
            }
        } catch (e: Exception) {
        }
    }
}
