package com.mirza.e_kart.customviews

import android.graphics.*
import kotlin.math.min


/**
 * Created by Mirza Ahmed Baig on 28/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */
object ImageConverter {

    fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap {
        val size = min(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, size, size)
        val rectF = RectF(rect)
        val roundPx = pixels.toFloat()

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }
}