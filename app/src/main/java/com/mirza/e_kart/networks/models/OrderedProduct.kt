package com.mirza.e_kart.networks.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Created by Mirza Ahmed Baig on 19/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */
@Parcelize
data class OrderedProduct(
    val product_id: Int,
    val product_name: String,
    val price: Double,
    val image: String,
    val status: Int,
    val current_address: String,
    var created_at: Long,
    var updated_at: Long
) : Parcelable