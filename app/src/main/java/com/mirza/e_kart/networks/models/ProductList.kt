package com.mirza.e_kart.networks.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductList(
    val product: ArrayList<ProductModel>
) : Parcelable