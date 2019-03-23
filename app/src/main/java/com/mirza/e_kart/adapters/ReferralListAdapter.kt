package com.mirza.e_kart.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mirza.e_kart.R
import com.mirza.e_kart.networks.models.UserDetails


class ReferralListAdapter(val dataArray: ArrayList<UserDetails>) :
    RecyclerView.Adapter<ReferralListAdapter.ProductViewHolder>() {
    private val TAG = ReferralListAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int) =
        ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.referral_item, parent, false))

    override fun getItemCount() = dataArray.size

    override fun onBindViewHolder(productViewHolder: ProductViewHolder, p1: Int) =
        productViewHolder.onBind(dataArray[p1])


    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName by lazy {
            itemView.findViewById<TextView>(R.id.user_name)
        }
        private val userMobile by lazy {
            itemView.findViewById<TextView>(R.id.user_mobile)
        }

        fun onBind(referralItem: UserDetails) {
            userName.text = referralItem.first_name + " " + referralItem.last_name
            userMobile.text = referralItem.mobile_number
        }
    }
}