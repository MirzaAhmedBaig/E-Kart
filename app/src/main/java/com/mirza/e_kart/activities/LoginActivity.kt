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
import android.view.View
import com.mirza.e_kart.R
import com.mirza.e_kart.customdialogs.ForgotPasswordDialog
import com.mirza.e_kart.extensions.isEmailValid
import kotlinx.android.synthetic.main.activity_login.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    private val TAG = LoginActivity::class.java.simpleName
    private val background by lazy {
        findViewById<View>(R.id.login_layout)
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
                ds.color = Color.parseColor("#FF415D")
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


}