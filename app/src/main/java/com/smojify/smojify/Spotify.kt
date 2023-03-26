package com.smojify.smojify

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream


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

fun reactToTrack(view: View, emoji:String) {
    Log.e("Function: ", "reactToTrack")
    Log.e("Reaction: ", emoji)
        spotifyAppRemote?.playerApi?.playerState?.setResultCallback { playerState ->
            val track_uri = current_trackid
            Log.e("Track: ", track_uri)
            val playlist = getplaylist(view, emoji)
            if (playlist != null) {
                // Log.e("Playlist Exist: ", "TRUE")
            } else {
                // Log.e("Playlist Exist: ", "FALSE")
                create_playlist(view, emoji)
                reactToTrack(view, emoji)
                return@setResultCallback
            }
            val currentTrackPosition = getTrackPosition(track_uri, playlist)
            //Log.e("Playlist", playlist.toString())
            Log.e("Track Position: ", currentTrackPosition.toString())
            runBlocking {
                if (currentTrackPosition == null) {
                    addTrackToPlaylist(view, track_uri, emoji, 0)
                } else if (currentTrackPosition > 0) {
                    removeTrackFromPlaylist(playlist.id, track_uri, currentTrackPosition)
                    addTrackToPlaylist(view, track_uri, emoji, currentTrackPosition - 1)
                }
            }
            LoadingScreen.hideLoading()
        }
    }


@OptIn(InternalAPI::class)
fun removeTrackFromPlaylist(playlistId: String, trackUri: String, trackPosition: Int) {
    Log.e("Function: ", "removeTrackFromPlaylist")
    val client = HttpClient() {
        install(ContentNegotiation) {
            gson()
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.spotify.com"
                path("/v1/playlists/" + playlistId + "/")
            }
            header("Authorization", "Bearer " + spotify_webtoken)
        }
    }
    runBlocking {
        client.attributes
        val response: HttpResponse = client.delete("tracks") {
            body = """
    {
        "tracks": [
            {
                "uri": "$trackUri",
                "positions": [$trackPosition]
            }
        ]
    }
    """
        }
        Log.e("RM_FROM_PLAYLIST",response.body())
    }
}


data class track_builder(val uri: String)
data class track_response(val track: track_builder)
data class tracks_list(val items:Array<track_response>)

fun getTrackPosition(trackUri: String, playlist: playlist_response): Int? {
    val client = HttpClient() {
        install(ContentNegotiation) {
            gson()
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.spotify.com"
                path("/v1/playlists/" + playlist.id + "/")
            }
            header("Authorization", "Bearer " + spotify_webtoken)
        }
    }

    var tracks: tracks_list? = null
    var result: Int? = null
    var offset = 0
    runBlocking {
        do {
            val response: HttpResponse = client.get("tracks") {
                parameter("limit", 50)
                parameter("offset", offset)
            }
            val json = response.body<tracks_list>()
            tracks = json
            for ((index, item) in tracks!!.items.withIndex()) {
                if (item.track.uri == trackUri) {
                    result = offset + index
                    break
                }
            }
            offset += 50
        } while (result == null && tracks!!.items.isNotEmpty())
    }

    return result
}


fun addTrackToPlaylist(view: View, track_uri:String, emoji:String, position: Int)
{
    Log.e("Function: ", "addTrackToPlaylist")
    val playlist = getplaylist(view, emoji)
    if (playlist == null) {
        Log.e("FUCK: ", "FUCK")
    }
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
            parameter("position", position)
            parameter("uris", track_uri)
        }
        Log.e("ADD_PLAYLIST",response.body())
        Toast.makeText(actictx, trackTitle + " by " + trackArtist + " added to " + emoji, Toast.LENGTH_SHORT).show()
        val json = response.body<playlists_list>()
    }
}

fun getplaylist(view: View, emoji: String):playlist_response
{
    val emojiName = getEmojiName(emoji)!!
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
        //Log.e("Playlist name", response.body())
        playlists =  json
    }
    val playlist = playlists.items.find { it.name.equals(emojiName) }
    if (playlist != null)
        return playlist
    return create_playlist(view, emoji)
}


fun create_playlist(view: View, emoji:String):playlist_response
{
    lateinit var respbody:playlist_response
    val emojiName = getEmojiName(emoji)!!
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
            setBody(playlist_builder(emojiName, false, false, "Playlist created by Smojify"))
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
            Log.e("Loading Screen", "Error while creating Dialog")
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.layout_loading_screen)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(cancelable)
        val textView = dialog!!.findViewById<TextView>(R.id.text)
        textView.text = text
        dialog!!.setOnShowListener { reactToTrack(
            convertView!!,
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