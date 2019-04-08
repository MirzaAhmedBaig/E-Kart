package com.mirza.e_kart.networks.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Created by Mirza Ahmed Baig on 28/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */
@Parcelize
data class Category(
    val id: Int,
    val name: String,
    val image: String,
    val brands: ArrayList<String>?,
    val created_at: String,
    val updated_at: String
) : Parcelable