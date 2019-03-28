package com.mirza.e_kart.networks.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Created by Mirza Ahmed Baig on 28/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */
@Parcelize
data class CategoriesModel(
    val category: ArrayList<Category>
) : Parcelable