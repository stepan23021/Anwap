package com.example.dmitriistepcenco.anwap

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader


private var document: Element? = null
private var finalUrl: String = ""
private val images: ArrayList<String> = ArrayList()
private const val mainUrl: String = "https://anwap.film"
private const val urlPattern = "https://anwap.film/films/load/on/MTI="

class MainActivity : AppCompatActivity() {

    private var filmNames: ArrayList<String> = ArrayList()
    private var urls: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        Async(mainUrl).execute()
        while (document == null) {
            //waiting for response from website
        }
        setupListView()
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val newUrl = urlPattern + urls[position].removePrefix("/films")
            finalUrl = ""
            Async2(newUrl).execute()
            while (finalUrl == "") {
                //waiting for response from website
            }
            val intent = Intent(this, ViewMovie::class.java)
            intent.putExtra("movieName", filmNames[position])
            intent.putExtra("url", finalUrl)
            startActivity(intent)
        }
    }

    private fun setupListView() {
        while (document == null) {
            //waiting for response from website)
        }
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

fun redirect(newUrl: String): String {
    var finalUrl = ""
    try {
        val obj = URL(newUrl)
        var conn = obj.openConnection() as HttpURLConnection
        conn.readTimeout = 5000
        conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8")
        conn.addRequestProperty("User-Agent", "Mozilla")
        conn.addRequestProperty("Referer", "google.com")

        var redirect = false
        var shitRedirect = false
        // normally, 3xx is redirect
        val status = conn.responseCode
        if (status == 200) {
            shitRedirect = true
        }

        if (status != HttpURLConnection.HTTP_OK) {
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER)
                redirect = true
        }

        if (shitRedirect) {
            finalUrl = conn.toString().removePrefix("com.android.okhttp.internal.huc.HttpURLConnectionImpl:")
            System.out.println(finalUrl)
        }

        if(redirect){
            val newNewUrl = conn.getHeaderField("Location")

            // open the new connnection again
            conn = URL(newNewUrl).openConnection() as HttpURLConnection
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8")
            conn.addRequestProperty("User-Agent", "Mozilla")
            conn.addRequestProperty("Referer", "google.com")
            println("Redirect to URL : $newNewUrl")
            return newNewUrl
        }

        val input = BufferedReader(
                InputStreamReader(conn.inputStream))
        val html = StringBuffer()

        while ((input.readLine()) != null) {
            html.append(input.readLine())
        }
        input.close()

        println("URL Content... \n" + html.toString())
        finalUrl = html.toString()
        println("Done")

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