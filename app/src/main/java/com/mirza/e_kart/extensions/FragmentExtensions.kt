import android.content.Context
import android.net.ConnectivityManager
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.mirza.e_kart.customdialogs.CustomAlertDialog
import com.mirza.e_kart.customdialogs.LoadingAlertDialog
import com.mirza.e_kart.extensions.shareApp
import com.mirza.e_kart.listeners.CustomDialogListener
import com.mirza.e_kart.networks.models.ProductList
import com.mirza.e_kart.networks.models.ProductModel

/**
 * Created by Mirza Ahmed Baig on 15/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */

private var progressDialog: LoadingAlertDialog? = null

fun Fragment.isNetworkAvailable(): Boolean {
    val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val activeNetworkInfo = connectivityManager?.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun Fragment.shareApp() {
    (activity as AppCompatActivity?)?.shareApp()
}

internal fun Fragment.showToast(message: String) {
    if (context != null) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

fun Fragment.showAlert(message: String, onSubmit: () -> Unit = {}) {
    val dialog = CustomAlertDialog().apply {
        setMessage(message)
        setSingleButton(true)
        setDismissListener(object : CustomDialogListener {
            override fun onPositiveClicked() {
                onSubmit()
            }

        })
    }
    dialog.show(fragmentManager, "alert_dialog")
    return
}


fun Fragment.hideLoadingAlert() {
    if (progressDialog != null && progressDialog!!.dialog != null && progressDialog!!.dialog.isShowing) {
        progressDialog!!.dismiss()
    }
}

fun Fragment.showLoadingAlert(message: String = "") {
    progressDialog = LoadingAlertDialog().apply {
        if (message.isNotBlank()) {
            setMessage(message)
        }
    }
    progressDialog!!.show(fragmentManager, "loading_alert_dialog_fragment")

}

fun Fragment.getMatchingItemsByCategory(productList: ArrayList<ProductModel>?, id: Int): ProductList? {
    val list = productList?.filter { it.category_id == id }
    return if (list != null && list.isNotEmpty()) {
        ProductList(ArrayList<ProductModel>().apply {
            addAll(list)
        })
    } else {
        null
    }
}
