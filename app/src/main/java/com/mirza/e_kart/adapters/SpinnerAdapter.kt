package com.mirza.e_kart.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.mirza.e_kart.R


class SpinnerAdapter(context: Context, textViewResourceId: Int, val objects: ArrayList<String>) :
    ArrayAdapter<String>(context, textViewResourceId, objects) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = LayoutInflater.from(parent.context).inflate(R.layout.spinner_list, parent, false)
        val label = row.findViewById(R.id.item) as TextView
        if (objects[0].contains("brand", true)) {
            label.setTextColor(Color.parseColor("#FFFFFF"))
        }
        label.text = objects[position]
        return row
    }
}