package com.mirza.e_kart.utils

import android.content.res.Resources
import android.util.DisplayMetrics


/**
 * Created by bala on 22-11-2017.
 */
internal fun dpToPx(dp: Int): Int {
    return ((dp * Resources.getSystem().displayMetrics.density).toInt())
}

internal fun dpToPx(dp: Float): Int {
    return ((dp * Resources.getSystem().displayMetrics.density).toInt())
}

internal fun pxToDp(px: Int): Int {
    return ((px / Resources.getSystem().displayMetrics.density).toInt())
}

fun dpToPxNew(dp: Int): Int {
    val displayMetrics = Resources.getSystem().displayMetrics
    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}
