package com.mirza.e_kart.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mirza.e_kart.R
import com.mirza.e_kart.networks.models.ProductModel

class ProductListAdapter(val dataArray: ArrayList<ProductModel>) :
    RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {
    private val TAG = ProductListAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int) =
        ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false))

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
        private val productIntrestText by lazy {
            itemView.findViewById<TextView>(R.id.product_interest_text)
        }
        private val productDiscription by lazy {
            itemView.findViewById<TextView>(R.id.product_description)
        }

        fun onBind(productInfo: ProductModel) {
            Glide.with(thumbnailImage.context)
                .load(productInfo.image)
                .into(thumbnailImage)
            productName.text = productInfo.name
            productPrice.text = productInfo.price
            productIntrestText.text = "Get this item at ${productInfo.interest}% interest rate"
            productDiscription.text = productInfo.description
        }
    }
}