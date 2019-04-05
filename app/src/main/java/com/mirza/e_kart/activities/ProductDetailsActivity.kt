package com.mirza.e_kart.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.mirza.e_kart.R
import com.mirza.e_kart.adapters.ProductCarouselAdapter
import com.mirza.e_kart.customdialogs.CustomAlertDialog
import com.mirza.e_kart.extensions.hideLoadingAlert
import com.mirza.e_kart.extensions.isNetworkAvailable
import com.mirza.e_kart.extensions.showLoadingAlert
import com.mirza.e_kart.extensions.showToast
import com.mirza.e_kart.listeners.ImageSliderListener
import com.mirza.e_kart.networks.ClientAPI
import com.mirza.e_kart.networks.models.ProductImages
import com.mirza.e_kart.networks.models.ProductModel
import com.mirza.e_kart.preferences.AppPreferences
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.product_details_contents.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductDetailsActivity : AppCompatActivity(), ImageSliderListener {

    private val TAG = ProductDetailsActivity::class.java.simpleName

    private val appPreferences by lazy {
        AppPreferences(this)
    }

    private val productDetails by lazy {
        intent.getParcelableExtra("productDetails") as ProductModel
    }

    private var productId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        productId = productDetails.id
        getProductImages()
    }

    override fun onBackPressed() {
        if (image_gallery.visibility == View.VISIBLE) {
            image_gallery.visibility = View.GONE
            dots_indicator_gallery.visibility = View.GONE
            buy_now.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }

    private fun setListeners() {
        back_button.setOnClickListener {
            finish()
        }
        buy_now.setOnClickListener {
            Intent(this, BuyingActivity::class.java).apply {
                putExtra("id", productId)
            }.also {
                startActivity(it)


            }
        }
    }

    private fun setProductCarousel(list: ArrayList<String>) {
        if (list.size == 0) {
            product_carousal.setBackgroundResource(R.mipmap.download)
        }
        product_carousal.adapter = ProductCarouselAdapter(this, list, this)
        dots_indicator.setViewPager(product_carousal)
        setGalleryAdapter(list)
    }

    private fun setProductDetails() {
        product_name.text = productDetails.name
        product_price.text = "\u20B9${productDetails.price}"
        processing_fees.text = "Processing Fees : \u20B9${productDetails.processing_fees}"
        product_interest_text.text = "Get this item at ${productDetails.interest}% interest rate"
        product_description.settings.javaScriptEnabled = true
        productDetails.description.let {
            product_description.loadData(productDetails.description, "text/html", "UTF-8")
        }
    }

    private fun setGalleryAdapter(list: ArrayList<String>) {
        image_gallery.adapter = ProductCarouselAdapter(this, list, null)
        dots_indicator_gallery.setViewPager(image_gallery)
    }

    override fun onItemClicked(position: Int) {
        image_gallery.setCurrentItem(position, false)
        image_gallery.visibility = View.VISIBLE
        dots_indicator_gallery.visibility = View.VISIBLE
        buy_now.visibility = View.GONE
    }


    private fun getProductImages() {
        if (!isNetworkAvailable()) {
            val dialog = CustomAlertDialog().apply {
                setMessage("Please check your internet.")
                setIcon(R.drawable.ic_warning)
                setSingleButton(true)
            }
            dialog.show(supportFragmentManager, "select_day_alert")
            return
        }
        showLoadingAlert()
        val call = ClientAPI.clientAPI.getProductImages("Bearer " + appPreferences.getJWTToken(), productDetails.id)
        Log.d(TAG, "Request URL : ${call.request().url()}")
        call.enqueue(object : Callback<ProductImages> {
            override fun onResponse(call: Call<ProductImages>, response: Response<ProductImages>) {
                hideLoadingAlert()
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse == null) {
                        showToast("ServerError")
                        return
                    }
                    Log.d(TAG, "Response Code : ${productResponse.images.size} id : ${productDetails.id}")
                    setProductCarousel(productResponse.images)
                    setListeners()
                    setProductDetails()

                } else {
                    showToast("Internal server error, please try again")
                }

                Log.d(TAG, "Response Code : ${response.code()}")
            }

            override fun onFailure(call: Call<ProductImages>, t: Throwable) {
                hideLoadingAlert()
                t.printStackTrace()
                showToast("Network Error!")
            }
        })
    }


}
