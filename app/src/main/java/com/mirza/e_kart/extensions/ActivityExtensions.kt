package com.mirza.e_kart.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.mirza.e_kart.R
import com.mirza.e_kart.customdialogs.CustomAlertDialog
import com.mirza.e_kart.customdialogs.LoadingAlertDialog
import com.mirza.e_kart.listeners.CustomDialogListener
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

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
    val shareIntent = Intent(Intent.ACTION_SEND)
    val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.e_kart)
    val path =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/Share.png"
    val out: OutputStream
    val file = File(path)
    try {
        out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    val bmpUri = FileProvider.getUriForFile(
        this,
        applicationContext.packageName + ".my.package.name.provider",
        file
    )
    shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
    shareIntent.putExtra(
        Intent.EXTRA_TEXT,
        "Hey please check this application https://play.google.com/store/apps/details?id=$packageName"
    )
    shareIntent.type = "image/png"
    startActivity(Intent.createChooser(shareIntent, "Share with"))
}


fun AppCompatActivity.showSnackBar(message: String) {
    val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
    val view = snackbar.view
    val tv = view.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
    tv.maxLines = 4
    snackbar.show()
}

fun AppCompatActivity.showSnackBar(message: String, actionMessage: String) {

    val snackbar = Snackbar
        .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
        .setAction(actionMessage) { }

    snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent))

    val sbView = snackbar.view
    val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
    textView.setTextColor(Color.WHITE)
    textView.maxLines = 4
    snackbar.show()
}

internal inline fun View.runOnUiThread(crossinline runnable: () -> Unit) {
    (context as Activity).runOnUiThread {
        runnable.invoke()
    }
}
