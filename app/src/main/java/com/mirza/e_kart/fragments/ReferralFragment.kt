package com.mirza.e_kart.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.mirza.e_kart.R
import com.mirza.e_kart.adapters.ReferralListAdapter
import com.mirza.e_kart.customdialogs.CustomAlertDialog
import com.mirza.e_kart.customdialogs.LoadingAlertDialog
import com.mirza.e_kart.networks.ClientAPI
import com.mirza.e_kart.networks.models.ReferralModel
import com.mirza.e_kart.networks.models.UserDetails
import com.mirza.e_kart.preferences.AppPreferences
import isNetworkAvailable
import kotlinx.android.synthetic.main.fragment_referral.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import showToast
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ReferralFragment : Fragment() {
    private val TAG = ReferralFragment::class.java.simpleName
    private val appPreferences by lazy {
        AppPreferences(context!!)
    }
    private val progressDialog by lazy {
        LoadingAlertDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_referral, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        referral_code.text = AppPreferences(context!!).getReferId()
        share_btn.setOnClickListener {
            shareApp()
        }

        getReferralList()

    }

    private fun setReferralAdapter(list: ArrayList<UserDetails>) {
        with(referred_list) {
            layoutManager = LinearLayoutManager(context)
            adapter = ReferralListAdapter(list)
        }
    }


    private fun getReferralList() {
        if (!isNetworkAvailable()) {
            val dialog = CustomAlertDialog().apply {
                setMessage("Please check your internet.")
                setIcon(R.drawable.ic_warning)
                setSingleButton(true)
            }
            dialog.show(fragmentManager, "select_day_alert")
            return
        }
        progressDialog.show(fragmentManager, "loading_alert_dailog")
        val call =
            ClientAPI.clientAPI.getReferrals("Bearer " + appPreferences.getJWTToken(), appPreferences.getUser().id)
        Log.d(TAG, "Request URL : ${call.request().url()} , ID : ${appPreferences.getUser().id}")
        call.enqueue(object : Callback<ReferralModel> {
            override fun onResponse(call: Call<ReferralModel>, response: Response<ReferralModel>) {
                hideAlert()
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse == null) {
                        showToast("NO referral found")
                        return
                    }
                    setReferralAdapter(productResponse.customer)

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        showToast(jObjError.getString("error"))
                    } catch (e: Exception) {
                        Log.d(TAG, "Error" + e.message)
                        showToast("ServerError!")
                        e.printStackTrace()
                    }
                }

                Log.d(TAG, "Response Code : ${response.code()}")
            }

            override fun onFailure(call: Call<ReferralModel>, t: Throwable) {
                hideAlert()
                t.printStackTrace()
                showToast("Network Error!")
            }
        })
    }


    private fun shareApp() {
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
            context!!,
            context!!.packageName + ".my.package.name.provider",
            file
        )
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey please check this application https://play.google.com/store/apps/details?id=${activity!!.packageName}"
        )
        shareIntent.type = "image/png"
        startActivity(Intent.createChooser(shareIntent, "Share with"))
    }

    private fun hideAlert() {
        if (progressDialog.dialog != null && progressDialog.dialog.isShowing) {
            progressDialog.dismiss()
        }
    }
}
