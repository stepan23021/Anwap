package com.example.dmitriistepcenco.anwap

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


/**
 *@author Dmitrii Stepcenco
 *@created 11 October 2018
 **/

class FilmListAdapter(private val filmNames:List<String>, private val urls: ArrayList<String>, private val activity: Activity) : BaseAdapter(){
    override fun getView(position: Int, child: View?, parent: ViewGroup): View? {
        var childView = child
        val layoutInflater: LayoutInflater
        val holder: Holder

        if (childView == null) {
            layoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            childView = layoutInflater.inflate(R.layout.row, parent, false)

            holder = Holder()
            holder.filmName = childView.findViewById(R.id.filmName)
            holder.url = childView.findViewById(R.id.url)
            holder.id = childView.findViewById(R.id.id)
            childView.tag = holder

        } else holder = childView.tag as Holder


        holder.id.text = (position + 1).toString()
        holder.filmName.text = filmNames[position]
        holder.url.text = urls[position]
        return childView
    }

    inner class Holder {
        lateinit var id: TextView
        lateinit var filmName: TextView
        lateinit var url: TextView
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
}