package com.mirza.e_kart.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.mirza.e_kart.R

/**
 * Created by Mirza Ahmed Baig on 15/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */

val TAG = "ActivityExtensions"


fun AppCompatActivity.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

internal fun AppCompatActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
