package com.mirza.e_kart.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mirza.e_kart.R
import com.mirza.e_kart.extensions.getStatusByCode
import com.mirza.e_kart.networks.MConfig
import com.mirza.e_kart.networks.models.OrderedProduct


/**
 * Created by Mirza Ahmed Baig on 19/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */

class OrderListAdapter(val dataArray: ArrayList<OrderedProduct>) :
    RecyclerView.Adapter<OrderListAdapter.ProductViewHolder>() {
    private val TAG = OrderListAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int) =
        ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false))

    override fun getItemCount() = dataArray.size

    override fun onBindViewHolder(productViewHolder: ProductViewHolder, p1: Int) =
        productViewHolder.onBind(dataArray[p1])


    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnailImage by lazy {
            itemView.findViewById<ImageView>(R.id.thumbnail)
        }
        private val productName by lazy {
            itemView.findViewById<TextView>(R.id.product_name)
        }
        private val productPrice by lazy {
            itemView.findViewById<TextView>(R.id.product_price)
        }
        private val productStatus by lazy {
            itemView.findViewById<TextView>(R.id.product_status)
        }

        fun onBind(orderedProduct: OrderedProduct) {
            Glide.with(thumbnailImage.context)
                .load(MConfig.IMAGE_BASE_URL + orderedProduct.image)
                .into(thumbnailImage)
            productName.text = orderedProduct.name
            productPrice.text = "\u20B9" + orderedProduct.price.toString()
            productStatus.text = getStatusByCode(orderedProduct.status)
        }
    }
}