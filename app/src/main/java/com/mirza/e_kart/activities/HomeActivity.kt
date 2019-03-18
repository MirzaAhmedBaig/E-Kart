package com.mirza.e_kart.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.BaseColumns
import android.support.design.widget.NavigationView
import android.support.v4.content.FileProvider
import android.support.v4.view.GravityCompat
import android.support.v4.widget.CursorAdapter
import android.support.v4.widget.SimpleCursorAdapter
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.mirza.e_kart.R
import com.mirza.e_kart.fragments.HomeFragment
import com.mirza.e_kart.fragments.OrderHistoryFragment
import com.mirza.e_kart.fragments.ReferralFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = HomeActivity::class.java.simpleName
    private val fromStrings = arrayOf("productName")
    private val toInts by lazy {
        IntArray(1).apply {
            set(0, R.id.suggestion_text)
        }
    }
    private val suggestionAdapter by lazy {
        SimpleCursorAdapter(
            this,
            R.layout.suggestion_layout,
            null,
            fromStrings,
            toInts,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
    }

    private val suggestionList = listOf(
        "Bauru", "Sao Paulo", "Sao Paulosdfsd", "Rio de Janeiro",
        "Bahia", "Mato Grosso", "Minas Gerais",
        "Tocantins", "Rio Grande do Sul"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpNavigationBar()
        moveToHomePage()
    }

    override fun onResume() {
        super.onResume()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        var searchView: SearchView? = null
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        setSearchView(searchView)
        return super.onCreateOptionsMenu(menu)
    }


    private fun setSearchView(searchView: SearchView?) {
        searchView?.suggestionsAdapter = suggestionAdapter
        searchView?.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(p0: Int): Boolean {
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = suggestionAdapter.getItem(position) as Cursor
                val query = cursor.getString(cursor.getColumnIndex("productName"))
                searchView.setQuery(query, true)
                return true
            }

        })
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d(TAG, "Query : $query")
                /*if (!searchView.isIconified) {
                    searchView.isIconified = true
                }*/
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG, "Text : $newText")
                newText?.let {
                    populateAdapter(it)
                }
                return false
            }

        })
    }

    private fun populateAdapter(query: String) {
        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, "productName"))
        suggestionList.forEachIndexed { index, it ->
            if (it.toLowerCase().startsWith(query.toLowerCase())) {
                cursor.addRow(arrayOf(index, it))
            }
        }
        suggestionAdapter.changeCursor(cursor)
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
        supportFragmentManager.beginTransaction().replace(R.id.main_layout, OrderHistoryFragment(), "home_fragment")
            .commit()
    }

    private fun moveToReferralPage() {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_layout)
        if (fragment is ReferralFragment)
            return
        supportFragmentManager.beginTransaction().replace(R.id.main_layout, ReferralFragment(), "home_fragment")
            .commit()
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
