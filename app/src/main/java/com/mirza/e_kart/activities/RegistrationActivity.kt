package com.mirza.e_kart.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.View
import com.mirza.e_kart.R
import com.mirza.e_kart.extensions.isEmailValid
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

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
            u_password.setSelection(show_password.text.toString().length)
        }

        register_btn.setOnClickListener {
            clearAllErrors()
            if (performValidation()) {
            }
        }
    }

    private fun performValidation(): Boolean {
        if (u_name_first.text.toString().isBlank()) {
            u_name_first.requestFocus()
            u_name_first.error = "Enter user name"
            return false
        }
        if (u_name_last.text.toString().isBlank()) {
            u_name_last.requestFocus()
            u_name_last.error = "Enter user name"
            return false
        }
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

        if (u_number.text.toString().trim().length != 10 || u_number.text.toString().contains("+")) {
            u_number.requestFocus()
            u_number.error = "Enter mobile number"
            return false
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
}
