package com.mirza.e_kart.networks.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductModel(
    val id: Int,
    val name: String,
    val colors: String?,
    val category_id: Int,
    val brand_id: Int,
    val brand_name: String?,
    val image: String,
    val description: String,
    val interest: Double,
    val price: Double,
    val processing_fees: Double,
    val created_at: Long,
    val updated_at: Long,
    val specification: String?
) : Parcelable