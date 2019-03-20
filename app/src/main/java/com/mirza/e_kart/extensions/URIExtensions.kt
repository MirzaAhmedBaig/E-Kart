package com.mirza.e_kart.extensions

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore


/**
 * Created by Mirza Ahmed Baig on 20/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */


fun Uri.getRealPathFromURI(contentResolver: ContentResolver): String? {
    var res: String? = null
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = contentResolver.query(this, proj, null, null, null)
    cursor?.let {
        if (cursor.moveToFirst()) {
            res = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
        }
        cursor.close()
        return res
    } ?: return null


}