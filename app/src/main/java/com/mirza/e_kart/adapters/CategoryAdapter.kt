package com.mirza.e_kart.adapters

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.mirza.e_kart.R
import com.mirza.e_kart.customviews.ImageConverter
import com.mirza.e_kart.networks.models.Category


/**
 * Created by Mirza Ahmed Baig on 28/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */


class CategoryAdapter(val dataList: ArrayList<Category>) : RecyclerView.Adapter<CategoryAdapter.ItemHolder>() {
    private val TAG = CategoryAdapter::class.java.simpleName
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) =
        ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false))

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.title.text = dataList[position].name

        Glide.with(holder.title.context)
            .asBitmap()
            .load(dataList[position].image)
            .into(object : BitmapImageViewTarget(holder.iconImage) {
                override fun setResource(resource: Bitmap?) {
                    resource?.let {
                        holder.iconImage.setImageBitmap(ImageConverter.getRoundedCornerBitmap(it, 200))
                    }
                }
            })

    }


    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImage: ImageView by lazy {
            itemView.findViewById<ImageView>(R.id.icon)
        }

        val title: TextView by lazy {
            itemView.findViewById<TextView>(R.id.name)
        }

        init {
            iconImage.setOnClickListener {

            }
        }
    }
}