import android.content.Context
import android.net.ConnectivityManager
import android.support.v4.app.Fragment
import android.widget.Toast

/**
 * Created by Mirza Ahmed Baig on 15/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */

fun Fragment.isNetworkAvailable(): Boolean {
    val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val activeNetworkInfo = connectivityManager?.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

internal fun Fragment.showToast(message: String) {
    if (context != null) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

internal inline fun Fragment.runOnUiThread(crossinline runnable: () -> Unit) {
    activity?.runOnUiThread {
        runnable.invoke()
    }
}