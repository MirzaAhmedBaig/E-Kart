package com.mirza.e_kart.activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.mirza.e_kart.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import android.support.v4.content.FileProvider
import com.mirza.e_kart.fragments.HomeFragment
import com.mirza.e_kart.fragments.OrderHistoryFragment
import com.mirza.e_kart.fragments.ReferralFragment


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = HomeActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setUpNavigationBar()
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onNavigationItemSelected")
        when (item.itemId) {
            R.id.nav_home -> {
                moveToHomePage()
            }
            R.id.nav_orders -> {
                moveToOrdersPage()
            }
            R.id.nav_referral -> {
                moveToReferralPage()
            }
            R.id.nav_share -> {
                shareApp()
            }
            R.id.nav_logout -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    private fun setUpNavigationBar() {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        toggle.isDrawerIndicatorEnabled = false
        toggle.toolbarNavigationClickListener = View.OnClickListener { drawer_layout.openDrawer(GravityCompat.START) }
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu)

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun moveToHomePage() {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_layout)
        if (fragment is HomeFragment)
            return
        supportFragmentManager.beginTransaction().replace(R.id.main_layout, HomeFragment(), "home_fragment").commit()
    }

    private fun moveToOrdersPage() {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_layout)
        if (fragment is OrderHistoryFragment)
            return
        supportFragmentManager.beginTransaction().replace(R.id.main_layout, OrderHistoryFragment(), "home_fragment").commit()
    }

    private fun moveToReferralPage() {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_layout)
        if (fragment is ReferralFragment)
            return
        supportFragmentManager.beginTransaction().replace(R.id.main_layout, ReferralFragment(), "home_fragment").commit()
    }

    private fun shareApp() {
        val shareIntent = Intent(android.content.Intent.ACTION_SEND)
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
            this,
            applicationContext.packageName + ".my.package.name.provider",
            file
        )
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey please check this application https://play.google.com/store/apps/details?id=$packageName"
        )
        shareIntent.type = "image/png"
        startActivity(Intent.createChooser(shareIntent, "Share with"))
    }
}
