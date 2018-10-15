package com.example.dmitriistepcenco.anwap

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.net.HttpURLConnection
import java.net.URL
import android.content.Context.INPUT_METHOD_SERVICE




private var document: Element? = null
private var finalUrl: String = ""
private val images: ArrayList<String> = ArrayList()
private const val mainUrl: String = "https://anwap.film"
private const val urlPattern = "https://anwap.film/films/load/on/MTU="

class MainActivity : AppCompatActivity() {
    private var maxPage: Int = Int.MAX_VALUE
    private var filmNames: ArrayList<String> = ArrayList()
    private var urls: ArrayList<String> = ArrayList()
    private var pageNumber = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        GetFilmListAsync(mainUrl).execute()
        var pagedMainURL: String
        setupListView()
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val newUrl = urlPattern + urls[position].removePrefix("/films")
            finalUrl = ""
            GetMovieURLAsync(newUrl).execute()
            while (finalUrl == "") {
                //waiting for response from website
            }
            val intent = Intent(this, ViewMovie::class.java)
            intent.putExtra("movieName", filmNames[position])
            intent.putExtra("url", finalUrl)
            startActivity(intent)
        }
        goButton.setOnClickListener {
            pageNumber = Integer.parseInt(pageNumberText.text.toString())
            pagedMainURL = "$mainUrl/films/p-${pageNumberText.text}"
            document = null
            GetFilmListAsync(pagedMainURL).execute()
            setupListView()
        }
        plusButton.setOnClickListener {
            pageNumber = Integer.parseInt(pageNumberText.text.toString())
            if (pageNumber < maxPage) pageNumber++
            pageNumberText.setText(pageNumber.toString(), TextView.BufferType.EDITABLE)
        }
        minusButton.setOnClickListener {
            pageNumber = Integer.parseInt(pageNumberText.text.toString())
            if (pageNumber > 1) pageNumber--
            pageNumberText.setText(pageNumber.toString(), TextView.BufferType.EDITABLE)
        }
    }

    private fun clearAll() {
        filmNames.clear()
        images.clear()
        urls.clear()
    }


    private fun setupListView() {
        clearAll()
        while (document == null) {
            //waiting for response from website
        }
        maxPage = Integer.parseInt(document!!.getElementsByClass("pages").select("a").last().attr("href").removePrefix("/films/p-"))
        filmNames = document!!.getElementsByClass("namefilm").eachText().toList() as ArrayList
        val elements: Elements = document!!.getElementsByClass("my_razdel film").select("a")
        val imageElements: Elements = document!!.getElementsByClass("screenfilm").select("img")
        val descriptionElements: ArrayList<String> = document!!.getElementsByClass("discripfilm").eachText().toList() as ArrayList<String>
        for (i in 0 until elements.size) {
            val attr = elements[i].attr("href")
            val imageAttr = imageElements[i].attr("src")
            if (!attr.contains("films/")) {
                filmNames.removeAt(i)
                descriptionElements.removeAt(i)
            } else {
                urls.add(attr)
                images.add(imageAttr)
            }
        }
        val itemsAdapter = FilmListAdapter(filmNames, descriptionElements, this, images)
        listView.adapter = itemsAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

fun redirect(url: String): String {
    var finalUrl = ""
    try {
        val movieURL = URL(url)
        var conn = movieURL.openConnection() as HttpURLConnection
        conn.readTimeout = 5000
        conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8")
        conn.addRequestProperty("User-Agent", "Mozilla")
        conn.addRequestProperty("Referer", "google.com")

        var redirect = false
        var notNormalRedirect = false
        // normally, 3xx is redirect
        val status = conn.responseCode
        if (status == 200) {
            notNormalRedirect = true
        }

        if (status != HttpURLConnection.HTTP_OK) {
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER)
                redirect = true
        }

        if (notNormalRedirect) {
            finalUrl = conn.toString().removePrefix("com.android.okhttp.internal.huc.HttpURLConnectionImpl:")
            System.out.println(finalUrl)
            return finalUrl
        }

        if (redirect) {
            val newNewUrl = conn.getHeaderField("Location")
            conn = URL(newNewUrl).openConnection() as HttpURLConnection
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8")
            conn.addRequestProperty("User-Agent", "Mozilla")
            conn.addRequestProperty("Referer", "google.com")
            println("Redirect to URL : $newNewUrl")
            return newNewUrl
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
    return finalUrl
}

internal class GetFilmListAsync(private val url: String) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg p0: Void?): Void? {
        try {
            document = Jsoup.connect(url).followRedirects(true).get().body()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}

internal class GetMovieURLAsync(private val url: String) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg p0: Void?): Void? {
        finalUrl = redirect(url)
        return null
    }
}