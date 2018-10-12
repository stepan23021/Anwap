package com.example.dmitriistepcenco.anwap

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import kotlinx.android.synthetic.main.activity_view_movie.*

class ViewMovie : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_movie)
        movieUrl.text = intent.getStringExtra("url")
    }
//    private lateinit var mMediaSession: MediaSessionCompat
//
//    public override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Create a MediaSessionCompat
//        mMediaSession = MediaSessionCompat(this, LOG_TAG).apply {
//
//            // Enable callbacks from MediaButtons and TransportControls
//            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
//                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
//
//            // Do not let MediaButtons restart the player when the app is not visible
//            setMediaButtonReceiver(null)
//
//            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
//            val stateBuilder = PlaybackStateCompat.Builder()
//                    .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE)
//            setPlaybackState(stateBuilder.build())
//
//            // MySessionCallback has methods that handle callbacks from a media controller
//            setCallback(MySessionCallback())
//        }
//
//        // Create a MediaControllerCompat
//        MediaControllerCompat(this, mMediaSession).also { mediaController ->
//            MediaControllerCompat.setMediaController(this, mediaController)
//        }
//    }
}
