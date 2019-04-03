package com.mirza.e_kart.activities

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.mirza.e_kart.R
import com.mirza.e_kart.extensions.timestampToDate
import com.mirza.e_kart.networks.MConfig
import com.mirza.e_kart.networks.models.OrderedProduct
import com.mirza.e_kart.preferences.AppPreferences
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.order_details_contents.*

class OrderDetailsActivity : AppCompatActivity() {

    private val orderDetails by lazy {
        intent.getParcelableExtra("productDetails") as OrderedProduct
    }

    private val appPreferences by lazy {
        AppPreferences(this)
    }

    private val dotsList by lazy {
        listOf(
            requested_dot,
            approved_dot,
            delivered_dot
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        setDetails()
        setListeners()
    }

    private fun setListeners() {
        back_button.setOnClickListener {
            finish()
        }
    }

    private fun setDetails() {
        product_name.text = orderDetails.name
        product_price.text = "\u20B9" + orderDetails.price
        Glide.with(this)
            .load(MConfig.PRODUCT_BASE_URL + orderDetails.image)
            .into(thumbnail)
        customer_name.text = appPreferences.getUserName()
        customer_address.text = orderDetails.current_address
        setStatus(orderDetails.status)
        request_date.text = timestampToDate(orderDetails.created_at)

    }


    private fun setStatus(code: Int) {
        if (code in 0..2) {
            vertical_progressbar.progress = 50 * code
            if (code == 2) {
                delivered_date.text = timestampToDate(orderDetails.updated_at)
                delivered_date.visibility = View.VISIBLE
            }
            (0..code)
                .forEach {
                    dotsList[it]?.setBackgroundResource(R.drawable.selected_dot)
                }
        } else if (code == 3) {
            vertical_progressbar.progressDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
            vertical_progressbar.progress = 50
            (0..1).forEach {
                dotsList[it]?.setBackgroundResource(R.drawable.rejected_dot)
            }
            approved_date.text = timestampToDate(orderDetails.updated_at)
            approved_text.text = "Rejected"
        }
    }


}
