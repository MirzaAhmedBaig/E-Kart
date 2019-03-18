package com.mirza.e_kart.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mirza.e_kart.preferences.AppPreferences

class SplashScreenActivity : AppCompatActivity() {

    val TAG = SplashScreenActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appPreferences = AppPreferences(this)
        when {
            appPreferences.isLoggedIn() -> gotoActivity(HomeActivity::class.java)
            else -> gotoActivity(LoginActivity::class.java)
        }
    }

    private fun gotoActivity(javaClassName: Class<*>) {
        val intent = Intent(this, javaClassName)
        startActivity(intent)
        finish()
    }
}
