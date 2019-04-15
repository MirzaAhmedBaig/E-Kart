package com.mirza.e_kart.extensions

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
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