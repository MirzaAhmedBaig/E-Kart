package com.mirza.e_kart.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mirza.e_kart.R
import com.mirza.e_kart.adapters.ProductCarouselAdapter
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        setProductCarousel()
        setListeners()
        setProductDetails()
    }

    private fun setListeners() {
        buy_now.setOnClickListener {
            Intent(this, BuyingActivity::class.java).apply {

            }.also {
                startActivity(it)
            }
        }
    }


    private fun setProductCarousel() {
        product_carousal.adapter = ProductCarouselAdapter(this, dummyPaths)
        dots_indicator.setViewPager(product_carousal)
    }

    private fun setProductDetails() {
        product_details.settings.javaScriptEnabled = true
        product_details.loadUrl("file:///android_asset/index.html")
    }

    private val dummyPaths = ArrayList<String>().apply {
        (0..5).forEach {
            add("https://rukminim1.flixcart.com/image/832/832/jl16s280/mobile/t/v/q/nokia-6-1-plus-na-original-imaf892edytmjfgg.jpeg?q=70")
        }
    }

}
