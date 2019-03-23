package com.mirza.e_kart.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mirza.e_kart.R
import com.mirza.e_kart.listeners.ImageSliderListener
import com.mirza.e_kart.networks.MConfig


/**
 * Created by Mirza Ahmed Baig on 19/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */

class ProductCarouselAdapter(
    private val context: Context,
    val imagePaths: ArrayList<String>,
    val sliderListener: ImageSliderListener?
) : PagerAdapter() {
    private val TAG = ProductCarouselAdapter::class.java.simpleName
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        Log.d(TAG, "instantiateItem")
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.product_carousel_item, container, false) as ViewGroup
        instantiateChild(position, layout)
        container.addView(layout)
        return layout
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj

    }

    override fun getCount(): Int {
        return imagePaths.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return position.toString()
    }

    private fun instantiateChild(position: Int, viewGroup: ViewGroup) {
        val imageView = viewGroup.findViewById<ImageView>(R.id.carousel_image)
        Glide.with(imageView.context)
            .load(MConfig.IMAGE_BASE_URL + imagePaths[position])
            .into(imageView)
        imageView.setOnClickListener {
            sliderListener?.onItemClicked(position)
        }
    }

}