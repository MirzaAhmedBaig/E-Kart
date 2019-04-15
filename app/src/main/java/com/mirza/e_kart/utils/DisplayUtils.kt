package com.mirza.e_kart.utils

import android.content.res.Resources


internal fun dpToPx(dp: Int): Int {
    return ((dp * Resources.getSystem().displayMetrics.density).toInt())
}
