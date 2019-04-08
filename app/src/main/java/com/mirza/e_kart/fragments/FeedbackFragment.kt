package com.mirza.e_kart.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mirza.e_kart.R
import com.mirza.e_kart.activities.HomeActivity
import com.mirza.e_kart.customdialogs.CustomAlertDialog
import com.mirza.e_kart.extensions.isEmailValid
import com.mirza.e_kart.extensions.moveToHomePage
import com.mirza.e_kart.networks.ClientAPI
import com.mirza.e_kart.networks.models.FeedbackModel
import com.mirza.e_kart.preferences.AppPreferences
import hideLoadingAlert
import isNetworkAvailable
import kotlinx.android.synthetic.main.fragment_feedback.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import showAlert
import showLoadingAlert
import showToast

class FeedbackFragment : Fragment() {

    private val TAG = FeedbackFragment::class.java.simpleName
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        u_email.setText(AppPreferences(context!!).getEmail())
        u_email.setSelection(u_email.text.length)
        setListeners()
    }

    private fun setListeners() {
        feedback_button.setOnClickListener {
            if (performValidation()) {
                sendFeedback()
            }
        }
    }


    private fun performValidation(): Boolean {
        if (!u_email.text.toString().isEmailValid()) {
            u_email.requestFocus()
            u_email.error = "Enter valid email"
            return false
        }
        if (subject.text.isBlank()) {
            subject.requestFocus()
            subject.error = "Enter feedback subject"
            return false
        }

        if (feedback_message.text.isBlank()) {
            feedback_message.requestFocus()
            feedback_message.error = "Enter feedback message"
            return false
        }
        return true
    }

    private fun sendFeedback() {
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
        val feedbackModel =
            FeedbackModel(u_email.text.toString(), subject.text.toString(), feedback_message.text.toString())
        val call =
            ClientAPI.clientAPI.sendFeedback("Bearer " + AppPreferences(context!!).getJWTToken(), feedbackModel)
        Log.d(TAG, "Request URL : ${call.request().url()}")
        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                hideLoadingAlert()
                if (response.isSuccessful) {
                    showAlert("Feedback successfully sent.") {
                        (activity as HomeActivity).moveToHomePage()
                    }
                } else {
                    showToast("Internal server error, please try again")
                }

                Log.d(TAG, "Response Code : ${response.code()}")
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                hideLoadingAlert()
                t.printStackTrace()
                showToast("Network Error!")
            }
        })
    }


}
