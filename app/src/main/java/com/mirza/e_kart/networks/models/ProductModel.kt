package com.mirza.e_kart.networks.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductModel(
    val id: Int,
    val name: String,
    val category_id: Int,
    val brand_id: Int,
    val image: String,
    val description: String,
    val interest: Double,
    val price: Double,
    val created_at: Long,
    val updated_at: Long,
    val specification: String
) : Parcelable