package com.mirza.e_kart.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import com.mirza.e_kart.R
import com.mirza.e_kart.customdialogs.CustomAlertDialog
import com.mirza.e_kart.customdialogs.ForgotPasswordDialog
import com.mirza.e_kart.extensions.*
import com.mirza.e_kart.networks.ClientAPI
import com.mirza.e_kart.networks.models.LoginModel
import com.mirza.e_kart.networks.models.LoginResponse
import com.mirza.e_kart.preferences.AppPreferences
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    private val TAG = LoginActivity::class.java.simpleName

    private val appPreferences by lazy {
        AppPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setListeners()
    }

    override fun onResume() {
        super.onResume()
        setRegistrationLink()
    }

    private fun setListeners() {
        login_ok.setOnClickListener {
            if (performValidation()) {
                performAuthentication()
            }
        }
        forget_password.setOnClickListener {
            showForgotPassword()
        }
    }

    private fun performValidation(): Boolean {
        if (!u_email.text.toString().isEmailValid()) {
            u_email.requestFocus()
            u_email.error = "Enter valid email"
            return false
        }
        if (u_password.text.isBlank()) {
            u_password.requestFocus()
            u_password.error = "Enter password"
            return false
        }
        return true
    }

    private fun setRegistrationLink() {
        val string = register_text.text.toString()
        val content = SpannableString(string)

        content.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View?) {
                startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
                finishAffinity()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = Color.parseColor("#D9905B")
                ds.isUnderlineText = false
                ds.linkColor = Color.WHITE
            }
        }, 10, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        register_text.text = content
        register_text.movementMethod = LinkMovementMethod.getInstance()
    }


    private fun showForgotPassword() {
        val forgotPasswordDialog = ForgotPasswordDialog()
        forgotPasswordDialog.show(supportFragmentManager, "hi_how")
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

    private fun performAuthentication() {
        val loginModel = LoginModel(u_email.text.toString(), u_password.text.toString())
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
        val call = ClientAPI.clientAPI.doLogin(loginModel)
        Log.d(TAG, "Request URL : ${call.request().url()}")
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                hideLoadingAlert()
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse == null) {
                        showToast("Error in logging in!")
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
                        response.code() == 404 -> {
                            u_email.requestFocus()
                            u_email.error = "User not found"
//                            showAlert("User not found")
                        }
                        response.code() == 500 -> {
                            showToast("Internal server error, please try again")
                        }
                        else -> {
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
}