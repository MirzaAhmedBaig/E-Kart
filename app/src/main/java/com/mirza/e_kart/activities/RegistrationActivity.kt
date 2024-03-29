package com.mirza.e_kart.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.mirza.e_kart.R
import com.mirza.e_kart.customdialogs.CustomAlertDialog
import com.mirza.e_kart.extensions.*
import com.mirza.e_kart.networks.ClientAPI
import com.mirza.e_kart.networks.models.LoginResponse
import com.mirza.e_kart.networks.models.SignupModel
import com.mirza.e_kart.preferences.AppPreferences
import kotlinx.android.synthetic.main.activity_registration.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Math.random


class RegistrationActivity : AppCompatActivity() {

    private val TAG = RegistrationActivity::class.java.simpleName

    private val appPreferences by lazy {
        AppPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        setListeners()
    }

    override fun onResume() {
        super.onResume()
        setLoginLink()
    }

    private fun setListeners() {
        show_password.setOnClickListener {
            if (show_password.text.toString() == "Show") {
                show_password.text = "Hide"
                u_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                show_password.text = "Show"
                u_password.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            if (u_password.text.isNotBlank())
                u_password.setSelection(show_password.text.toString().length)
        }

        register_btn.setOnClickListener {
            clearAllErrors()
            if (performValidation()) {
                performRegistrationRequest()
            }
        }

        u_name_first.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                Log.d(TAG, "onKey1")
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_FORWARD) {
                    Log.d(TAG, "onKey2")
                    Handler().postDelayed({
                        u_name_last.requestFocus()
                    }, 100)
                    return true
                }
                return false
            }
        })


        request_otp.setOnClickListener {
            if (!u_number.text.toString().matches(Regex("^[6-9][0-9]{9}$"))) {
                u_number.requestFocus()
                u_number.error = "Enter valid mobile number"
            } else {
                request_otp.isEnabled = false
                sendOTP()
                Handler().postDelayed({
                    request_otp.isEnabled = true
                }, 30000)
            }
        }

    }

    private fun performValidation(): Boolean {
        if (u_name_first.text.toString().isBlank()) {
            u_name_first.requestFocus()
            u_name_first.error = "Enter first name"
            return false
        }
        if (u_name_last.text.toString().isBlank()) {
            u_name_last.requestFocus()
            u_name_last.error = "Enter last name"
            return false
        }
        if (!u_email.text.toString().isEmailValid()) {
            u_email.requestFocus()
            u_email.error = "Enter valid email"
            return false
        }
        if (u_password.text.isBlank() || u_password.text.length < 6) {
            u_password.requestFocus()
            u_password.error = "Enter valid password(min 6 char)"
            return false
        }

        if (!u_number.text.toString().matches(Regex("^[6-9][0-9]{9}$"))) {
            u_number.requestFocus()
            u_number.error = "Enter valid mobile number"
            return false
        }

        if (u_otp.text.toString().trim().length != 6) {
            u_otp.requestFocus()
            u_otp.error = "Enter valid OTP"
            return false
        } else {
            if (!validOTP()) {
                u_otp.requestFocus()
                u_otp.error = "Enter valid OTP"
                return false
            }
        }
        return true
    }

    private fun clearAllErrors() {
        u_name_first.error = null
        u_name_last.error = null
        u_email.error = null
        u_password.error = null
        u_number.error = null
    }

    private fun setLoginLink() {
        val string = login_text.text.toString()
        val content = SpannableString(string)

        content.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View?) {
                startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
                finishAffinity()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = Color.parseColor("#D9905B")
                ds.isUnderlineText = false
                ds.linkColor = Color.WHITE
            }
        }, 24, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        login_text.text = content
        login_text.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun completedLogin(response: LoginResponse) {
        appPreferences.setUser(response.user)
        appPreferences.setLoggedIn(true)
        appPreferences.setEmail(response.user.email)
        appPreferences.setReferId(response.user.referal_id)
        appPreferences.setUserName(response.user.first_name + " " + response.user.last_name)
        appPreferences.setJWTToken(response.access_token)
        startActivity(Intent(this, HomeActivity::class.java))
        finishAffinity()
    }

    private fun performRegistrationRequest() {
        val userDetails = SignupModel(
            u_name_first.text.toString(),
            u_name_last.text.toString(),
            u_email.text.toString(),
            u_number.text.toString().toBigInteger(),
            u_password.text.toString(),
            u_reference_code.text.toString()
        )

        if (!isNetworkAvailable()) {
            val dialog = CustomAlertDialog().apply {
                setMessage("Please check your internet.")
                setIcon(R.drawable.ic_warning)
                setSingleButton(true)
            }
            dialog.show(supportFragmentManager, "select_day_alert")
            return
        }
        showLoadingAlert()
        val call = ClientAPI.clientAPI.doSignUp(userDetails)
        Log.d(TAG, "Request URL : ${call.request().url()}")
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                hideLoadingAlert()
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse == null) {
                        showToast("Please try after sometime")
                        return
                    }

                    Log.d(TAG, "Response : ${response.body()}  ${response.body()?.user}")
                    completedLogin(loginResponse)
                } else {
                    when {
                        response.code() == 400 -> {
                            u_password.requestFocus()
                            u_password.error = "Invalid password"
                        }
                        response.code() == 401 -> {

                             u_email.requestFocus()
                            u_email.error = "Email already exist"
                        }
                        response.code() == 500 -> {
                            showToast("Internal server error, please try again")
                        }
                        else -> {
                            Log.d(TAG, "Response Code : ${response.code()}")
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                showToast(jObjError.getString("error"))
                            } catch (e: Exception) {
                                Log.d(TAG, "Error" + e.message)
                                showToast("ServerError!")
                                e.printStackTrace()
                            }
                        }
                    }
                }

                Log.d(TAG, "Response Code : ${response.code()}")
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                hideLoadingAlert()
                t.printStackTrace()
                showToast("Network Error!")
            }
        })
    }


    private val otp: Int = (100000 + random() * 900000).toInt()
    private fun sendOTP() {
        val mobileNumber = u_number.text.toString()
        if (!isNetworkAvailable()) {
            val dialog = CustomAlertDialog().apply {
                setMessage("Please check your internet.")
                setIcon(R.drawable.ic_warning)
                setSingleButton(true)
            }
            dialog.show(supportFragmentManager, "select_day_alert")
            return
        }
        showLoadingAlert()
        val call =
            ClientAPI.clientAPI.sendOTP(mobileNumber = mobileNumber, msg = "OTP for NFPL account registration is $otp")
        Log.d(
            TAG,
            "Request URL : ${call.request().url()} , Mobile : $mobileNumber Message = OPT for NFPL account registration is $otp"
        )
        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                hideLoadingAlert()
                if (response.isSuccessful) {
                    showAlert("OTP sent to $mobileNumber\nPlease wait for 30 second before requesting OTP again.")
                    Log.d(TAG, "Response body ${response.body()}")
                    u_otp.isEnabled = true
                } else {
                    showToast("Please try again")
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

    private fun validOTP(): Boolean {
        return otp == u_otp.text.toString().toInt()
    }

}



