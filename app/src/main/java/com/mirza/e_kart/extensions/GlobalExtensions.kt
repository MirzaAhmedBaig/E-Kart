package com.mirza.e_kart.extensions

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.mirza.e_kart.classes.Compressor
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


/**
 * Created by Mirza Ahmed Baig on 15/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */

fun Any?.toStringWithGSON(): String {
    return Gson().toJson(this)
}


fun compressFiles(context: Context, paths: ArrayList<String>) {
    Log.d("TAG", "Time : " + System.currentTimeMillis())
    val tempFolder = File(File(paths[0]).parent + File.separator + "temp")
    if (!tempFolder.exists())
        tempFolder.mkdir()
    paths.forEach {
        val file = File(it)
        val compressedImage = Compressor(context)
            .setDestinationDirectoryPath(tempFolder.path)
            .compressToFile(file)
        file.delete()
        val out: OutputStream = FileOutputStream(File(it))
        out.write(compressedImage.readBytes())
        out.close()
        compressedImage.delete()
    }
    Log.d("TAG", "Time : " + System.currentTimeMillis())
}