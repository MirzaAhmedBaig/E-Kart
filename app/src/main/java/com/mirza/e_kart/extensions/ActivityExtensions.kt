package com.mirza.e_kart.extensions

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.mirza.e_kart.BuildConfig
import com.mirza.e_kart.R
import com.mirza.e_kart.customdialogs.CustomAlertDialog
import com.mirza.e_kart.customdialogs.LoadingAlertDialog
import com.mirza.e_kart.listeners.CustomDialogListener
import com.mirza.e_kart.preferences.AppPreferences
import java.io.File
import java.io.FileOutputStream


/**
 * Created by Mirza Ahmed Baig on 15/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */

val TAG = "ActivityExtensions"


private var progressDialog: LoadingAlertDialog? = null

fun AppCompatActivity.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun AppCompatActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.hideLoadingAlert() {
    if (progressDialog != null && progressDialog!!.dialog != null && progressDialog!!.dialog.isShowing) {
        progressDialog!!.dismiss()
    }
}

fun AppCompatActivity.showLoadingAlert(message: String = "") {
    progressDialog = LoadingAlertDialog().apply {
        if (message.isNotBlank()) {
            setMessage(message)
        }
    }
    progressDialog!!.show(supportFragmentManager, "loading_alert_dialog")

}

fun AppCompatActivity.showAlert(message: String) {
    val dialog = CustomAlertDialog().apply {
        setMessage(message)
        setSingleButton(true)
    }
    dialog.show(supportFragmentManager, "alert_dialog")
    return
}

fun AppCompatActivity.showAlert(message: String, setIcon: Boolean = true, onDone: () -> Unit = {}) {
    val dialog = CustomAlertDialog().apply {
        setMessage(message)
        setSingleButton(true)
        if (setIcon)
            setIcon(R.drawable.ic_warning)
        setDismissListener(object : CustomDialogListener {
            override fun onPositiveClicked() {
                onDone.invoke()
            }
        })
    }
    dialog.show(supportFragmentManager, "alert_dialog_custom")
    return
}


fun AppCompatActivity.shareApp() {
    try {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.nfpl_share)
        val outputFile = File(cacheDir, "nfpl.jpeg")
        val outPutStream = FileOutputStream(outputFile)
        bitmap.compress(CompressFormat.JPEG, 100, outPutStream)
        outPutStream.flush()
        outPutStream.close()
        outputFile.setReadable(true, false)


        val bmpUri = FileProvider.getUriForFile(
            this,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            outputFile
        )
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "I found a wonderful free online shopping app .They provide goods on credits with easy EMI's. Their delivery system in so fast that they deliver item in 90 minutes on your doorstep. \n" +
                    "Download the app here and use my referral code :${AppPreferences(this).getReferId()}\nhttps://play.google.com/store/apps/details?id=$packageName"
        )
        shareIntent.type = "image/jpeg"
        startActivity(shareIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(this, "error", Toast.LENGTH_LONG).show()
    }


}
