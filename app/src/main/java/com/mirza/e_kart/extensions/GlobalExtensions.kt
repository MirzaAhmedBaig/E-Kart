package com.mirza.e_kart.extensions

import com.google.gson.Gson


/**
 * Created by Mirza Ahmed Baig on 15/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */

fun Any?.toStringWithGSON(): String {
    return Gson().toJson(this)
}