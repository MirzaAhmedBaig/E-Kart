package com.mirza.e_kart.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mirza.e_kart.R
import com.mirza.e_kart.adapters.ReferralListAdapter
import com.mirza.e_kart.customdialogs.CustomAlertDialog
import com.mirza.e_kart.networks.ClientAPI
import com.mirza.e_kart.networks.models.ReferralModel
import com.mirza.e_kart.networks.models.UserDetails
import com.mirza.e_kart.preferences.AppPreferences
import hideLoadingAlert
import isNetworkAvailable
import kotlinx.android.synthetic.main.fragment_referral.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import shareApp
import showLoadingAlert
import showToast

class ReferralFragment : Fragment() {
    private val TAG = ReferralFragment::class.java.simpleName
    private val appPreferences by lazy {
        AppPreferences(context!!)
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
        showLoadingAlert()
        val call =
            ClientAPI.clientAPI.getReferrals("Bearer " + appPreferences.getJWTToken(), appPreferences.getUser().id)
        Log.d(TAG, "Request URL : ${call.request().url()} , ID : ${appPreferences.getUser().id}")
        call.enqueue(object : Callback<ReferralModel> {
            override fun onResponse(call: Call<ReferralModel>, response: Response<ReferralModel>) {
                hideLoadingAlert()
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse == null) {
                        showToast("NO referral found")
                        return
                    }
                    setReferralAdapter(productResponse.customer)

                } else {
                    showToast("Internal server error, please try again")
                }

                Log.d(TAG, "Response Code : ${response.code()}")
            }

            override fun onFailure(call: Call<ReferralModel>, t: Throwable) {
                hideLoadingAlert()
                t.printStackTrace()
                showToast("Network Error!")
            }
        })
    }


}
