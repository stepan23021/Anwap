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
import org.jsoup.Connection
import org.jsoup.select.Elements
import java.lang.Exception


private var document: Element? = null
private var response: String = ""

private const val url: String = "https://anwap.film"

class MainActivity : AppCompatActivity() {
    private var filmNames: ArrayList<String> = ArrayList()
    private var urls: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        Async(url).execute()
        listView.onItemClickListener = AdapterView.OnItemClickListener {_,_,position,_ ->
            val newUrl = url+urls[position]
            document = null
            Async(newUrl).execute()
            while(document==null){

            }
            findUrl()
        }
    }

    private fun findUrl() {
        //val videoURL = document!!.getElementsByClass("blms")[0]
        val url: String = "https://anwap.film/films/load/on/MTE=/20646"
        Async2(url).execute()
        Log.i("response", response)
    }

    override fun onResume() {
        setupListView()
        super.onResume()
    }

    private fun setupListView() {
        while (document == null) {
            Log.i("loading", "Loading...")
        }
        filmNames = document!!.getElementsByClass("namefilm").eachText().toList() as ArrayList
        val elements: Elements = document!!.getElementsByClass("my_razdel film").select("a")

        for (i in 0 until elements.size) {
            val attr = elements[i].attr("href")
            if(!attr.contains("films/")){
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

internal class Async(private val url: String) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg p0: Void?): Void? {
        try {
            document = Jsoup.connect(url).get().body()
        } catch (e: Exception) {

        }
        return null
    }

    override fun onPostExecute(result: Void?) {

    }
}

internal class Async2(private val url: String) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg p0: Void?): Void? {
        try {
            response = Jsoup.connect(url).followRedirects(true).execute().url().toString()
        } catch (e: Exception) {

        }
        return null
    }

    override fun onPostExecute(result: Void?) {

    }
}