package com.mirza.e_kart.classes

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.IOException


/**
 * Created by Mirza Ahmed Baig on 20/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */

class Compressor(context: Context) {
    //max width and height values of the compressed image is taken as 612x816
    private var maxWidth = 612
    private var maxHeight = 816
    private var compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    private var quality = 80
    private var destinationDirectoryPath: String? = null

    init {
        destinationDirectoryPath = context.cacheDir.path + File.separator + "images"
    }

    fun setDestinationDirectoryPath(destinationDirectoryPath: String): Compressor {
        this.destinationDirectoryPath = destinationDirectoryPath
        return this
    }

    @Throws(IOException::class)
    @JvmOverloads
    fun compressToFile(imageFile: File, compressedFileName: String = imageFile.name): File {
        return ImageUtil.compressImage(
            imageFile, maxWidth, maxHeight, compressFormat, quality,
            destinationDirectoryPath + File.separator + compressedFileName
        )
    }

}