package com.example.dmitriistepcenco.anwap

import android.app.ProgressDialog
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_view_movie.*


class ViewMovie : AppCompatActivity() {
    private val progressDialog: ProgressDialog? = null
    private var videoPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_movie)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        videoPath = intent.getStringExtra("url")
        movieName.text = intent.getStringExtra("movieName")
        ViewMovie.progressDialog = ProgressDialog.show(this, "Загрузка", "Buffering video...", true)
        ViewMovie.progressDialog!!.setCancelable(true)
        ViewMovie.progressDialog!!.window.setGravity(Gravity.BOTTOM)
        viewMovie()
    }

    private fun viewMovie() {
        try {
            window.setFormat(PixelFormat.TRANSLUCENT)
            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            val video = Uri.parse(videoPath)
            videoView?.setMediaController(mediaController)
            videoView?.setVideoURI(video)
            videoView?.requestFocus()
            videoView?.setOnPreparedListener {
                ViewMovie.progressDialog?.dismiss()
                videoView!!.start()
            }
            videoView?.keepScreenOn = true
        } catch (e: Exception) {
            progressDialog!!.dismiss()
            println("Video Play Error :" + e.toString())
            finish()
        }
    }

    companion object {
        internal var progressDialog: ProgressDialog? = null
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}




