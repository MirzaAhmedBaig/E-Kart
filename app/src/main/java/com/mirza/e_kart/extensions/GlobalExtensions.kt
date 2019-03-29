package com.mirza.e_kart.extensions

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.mirza.e_kart.classes.Compressor
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Mirza Ahmed Baig on 15/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */

fun Any?.toStringWithGSON(): String {
    return Gson().toJson(this)
}


fun compressFiles(context: Context, paths: Array<String?>) {
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

fun timestampToDate(s: Long): String {
    return try {
        val sdf = SimpleDateFormat("D, mm-MMM yyyy")
        val netDate = Date(s)
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}

fun getCurrentTimeString(): String {
    return try {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val netDate = Date(System.currentTimeMillis())
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}


fun getStatusByCode(code: Int): String {
    return when (code) {
        0 -> {
            "Requested"
        }
        1 -> {
            "Approved"
        }
        2 -> {
            "Delivered"
        }
        else -> {
            "Rejected"
        }

    }
}