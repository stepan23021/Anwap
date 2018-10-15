package com.example.dmitriistepcenco.anwap

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso


/**
 *@author Dmitrii Stepcenco
 *@created 11 October 2018
 **/

class FilmListAdapter(private val filmNames: List<String>,
                      private val description: ArrayList<String>,
                      private val activity: Activity,
                      private val images: ArrayList<String>) : BaseAdapter() {
    override fun getView(position: Int, child: View?, parent: ViewGroup): View? {
        var childView = child
        val layoutInflater: LayoutInflater
        val holder: Holder

        if (childView == null) {
            layoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            childView = layoutInflater.inflate(R.layout.row, parent, false)

            holder = Holder()
            holder.filmName = childView.findViewById(R.id.filmName)
            holder.url = childView.findViewById(R.id.description)
            holder.image = childView.findViewById(R.id.imageView)
            childView.tag = holder

        } else holder = childView.tag as Holder

        holder.filmName.text = filmNames[position]
        holder.url.text = description[position]
        Picasso.with(activity).load(images[position]).fit().centerInside().into(holder.image)
        return childView
    }


    override fun getItem(p0: Int): Any? {
        return null
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return filmNames.size
    }

    inner class Holder {
        lateinit var filmName: TextView
        lateinit var url: TextView
        lateinit var image: ImageView
    }
}