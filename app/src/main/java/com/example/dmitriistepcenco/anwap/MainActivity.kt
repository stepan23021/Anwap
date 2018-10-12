package com.example.dmitriistepcenco.anwap

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import android.os.AsyncTask
import android.util.Log
import android.widget.AdapterView
import kotlinx.android.synthetic.main.content_main.*
import org.jsoup.select.Elements
import java.lang.Exception
import java.net.URL
import java.net.HttpURLConnection


private var document: Element? = null
private var finalUrl: String = ""
private const val url: String = "https://anwap.film"
private const val urlPattern = "https://anwap.film/films/load/on/MTI="

class MainActivity : AppCompatActivity() {
    private var filmNames: ArrayList<String> = ArrayList()
    private var urls: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        Async(url).execute()
        while(document==null){

        }
        setupListView()
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val newUrl = urlPattern + urls[position].removePrefix("/films")
            document = null
            finalUrl = ""
            Async2(newUrl).execute()
            while(finalUrl==""){

            }
            val intent = Intent(this,ViewMovie::class.java)
            intent.putExtra("url", finalUrl)
            startActivity(intent)
        }
    }


//    override fun onResume() {
//        setupListView()
//        super.onResume()
//    }

    private fun setupListView() {
        while (document == null) {
            Log.i("loading", "Loading...")
        }
        filmNames = document!!.getElementsByClass("namefilm").eachText().toList() as ArrayList
        val elements: Elements = document!!.getElementsByClass("my_razdel film").select("a")

        for (i in 0 until elements.size) {
            val attr = elements[i].attr("href")
            if (!attr.contains("films/")) {
                filmNames.removeAt(i)
            } else urls.add(attr)
        }
        val itemsAdapter = FilmListAdapter(filmNames, urls, this)
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

fun redirect(newUrl: String): String {
    var finalUrl = ""
    try {

        val obj = URL(newUrl)
        val conn = obj.openConnection() as HttpURLConnection
        conn.readTimeout = 5000
        conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8")
        conn.addRequestProperty("User-Agent", "Mozilla")
        conn.addRequestProperty("Referer", "google.com")

        println("Request URL ... $newUrl")

        var redirect = false

        // normally, 3xx is redirect
        val status = conn.responseCode
        if (status == 200) {
            redirect = true
        }
        if (status != HttpURLConnection.HTTP_OK) {
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER)
                redirect = true
        }

        if (redirect) {
            finalUrl = conn.toString().removePrefix("com.android.okhttp.internal.huc.HttpURLConnectionImpl:")
            System.out.println(finalUrl)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return finalUrl
}

internal class Async(private val url: String) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg p0: Void?): Void? {
        try {
            document = Jsoup.connect(url).followRedirects(true).get().body()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}

internal class Async2(private val newUrl: String) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg p0: Void?): Void? {
        finalUrl = redirect(newUrl)
        return null
    }
}