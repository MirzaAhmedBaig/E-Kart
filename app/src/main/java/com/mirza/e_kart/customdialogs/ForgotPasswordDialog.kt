package com.mirza.e_kart.customdialogs

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mirza.e_kart.R
import com.mirza.e_kart.extensions.blur
import kotlinx.android.synthetic.main.forgot_password_layout.*
import kotlinx.android.synthetic.main.forgot_password_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordDialog : android.support.v4.app.DialogFragment() {
    val TAG = ForgotPasswordDialog::class.java.simpleName
    private var bitmap: Bitmap? = null
    private val uiScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(
            DialogFragment.STYLE_NO_TITLE, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                android.R.style.Theme_Material_Light_NoActionBar
            } else {
                android.R.style.Theme_DeviceDefault_Light_NoActionBar
            }
        )
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#4B000000")))
        return inflater.inflate(R.layout.forgot_password_layout, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        bitmap?.let {
            Log.d(TAG, "Ca")
            view.frameLayout2.background = BitmapDrawable(resources, it)
            uiScope.launch {
                view.frameLayout2.background = BitmapDrawable(resources, it.blur(this@ForgotPasswordDialog.context!!))
            }
        }
    }

    private fun initListeners() {
        doneButton.setOnClickListener {
            dismiss()
        }
        submitEmailButton.setOnClickListener {
            /*val email = editText.text.trim().toString()
            Log.d(TAG, "Email is $email")
            if (email.isEmail()) {
                forgetPassword(email)
            } else {
                showToast("Enter correct email!")
            }*/
        }
    }

    private fun showErrorMessage(message: String) {
        emailSentTV?.text = message
    }

    fun setBlurBackground(bitmap: Bitmap) {
        this.bitmap = bitmap
    }

    private fun forgetPassword(email: String) {
        /*isCancelable = false
        val emailPacket = Email()
        emailPacket.email = email
        enterEmailGroup.visibility = View.GONE
        statusGroup.visibility = View.GONE
        loadingGroup.visibility = View.VISIBLE
        val call = ClientAPI.meditiationClientAPI.forgetPassword(emailPacket)
        call.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                loadingGroup?.visibility = View.GONE
                statusGroup?.visibility = View.VISIBLE
                if (response.isSuccessful) {
                    val updateResponse = response.body()
                    if (updateResponse == null) {
                        showErrorMessage("Unknown Error")
                        return
                    }
                } else {
                    response.errorBody()?.string()?.let {
                        val json = JSONObject(it)
                        val message = json.getString("message")
                        showErrorMessage(message)
                    }
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                loadingGroup?.visibility = View.GONE
                statusGroup?.visibility = View.VISIBLE
                showErrorMessage("Network Error!")
                t.printStackTrace()
            }
        })*/
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        manager?.beginTransaction()?.add(this, tag)?.commitAllowingStateLoss()
    }
}