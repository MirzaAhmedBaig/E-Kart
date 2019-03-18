package com.mirza.e_kart.extensions

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import com.mirza.e_kart.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

/**
 * Created by Mirza Ahmed Baig on 15/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */

suspend fun Bitmap.blur(context: Context): Bitmap {
    val uiScope = CoroutineScope(Dispatchers.Main)
    val a = uiScope.async {
        val outputBitmap = Bitmap.createBitmap(this@blur)
        val renderScript = RenderScript.create(context)
        val tmpIn = Allocation.createFromBitmap(renderScript, this@blur)
        val tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap)
        //Intrinsic Gausian blur filter
        val theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        theIntrinsic.setRadius(15f)
        theIntrinsic.setInput(tmpIn)
        theIntrinsic.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)
        outputBitmap
    }
    return a.await()
}

suspend fun Bitmap.overlay(bmp2: Bitmap, left: Int, faceScale: Float, faceTranslate: Float): Bitmap {
    Log.d("BITMAP", "1 faceTranslate:$faceTranslate")
    Log.d("BITMAP", "2 bmp2 height ${bmp2.height} and width ${bmp2.width}")
    val uiScope = CoroutineScope(Dispatchers.Main)
    val a = uiScope.async {
        val bmOverlay = Bitmap.createBitmap(this@overlay.width, this@overlay.height, this@overlay.config)
        val canvas = Canvas(bmOverlay)
        canvas.drawBitmap(this@overlay, 0f, 0f, null)
        val bFinal =
            Bitmap.createScaledBitmap(bmp2, (bmp2.width * faceScale).toInt(), (bmp2.height * faceScale).toInt(), true)
        canvas.drawBitmap(
            bFinal,
            (left.toFloat()) + (bmp2.width * Math.abs(1 - faceScale) / 2).toInt(),
            faceTranslate,
            null
        )
        bmOverlay
    }
    return a.await()
}

inline fun <CFragment : Fragment> CFragment.withArgs(argsBuilder: Bundle.() -> Unit): CFragment =
    this.apply { arguments = Bundle().apply(argsBuilder) }

inline fun <IIntent : Intent> IIntent.withBundle(bundleBuilder: Bundle.() -> Unit): IIntent =
    this.apply { putExtra("bundle", Bundle().apply(bundleBuilder)) }


fun getBitmap(view: View, includeBackground: Boolean = false): Bitmap =
    with(view) {
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        if (includeBackground) {
            val paint2 = Paint()
            paint2.color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resources.getColor(R.color.app_background_start, null)
            } else {
                resources.getColor(R.color.app_background_start)
            }
            paint2.style = Paint.Style.FILL
            c.drawPaint(paint2)
        }
        layout(left, top, right, bottom)
        draw(c)
        return b
    }