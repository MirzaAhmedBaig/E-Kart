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
import com.mirza.e_kart.customdialogs.CustomAlertDialog
import com.mirza.e_kart.customdialogs.LoadingAlertDialog
import com.mirza.e_kart.extensions.isNetworkAvailable
import com.mirza.e_kart.extensions.showToast
import com.mirza.e_kart.extensions.suggestionList
import com.mirza.e_kart.fragments.HomeFragment
import com.mirza.e_kart.fragments.OrderHistoryFragment
import com.mirza.e_kart.fragments.ReferralFragment
import com.mirza.e_kart.listeners.CustomDialogListener
import com.mirza.e_kart.listeners.RefreshProductListener
import com.mirza.e_kart.networks.ClientAPI
import com.mirza.e_kart.networks.models.ProductList
import com.mirza.e_kart.networks.models.ProductModel
import com.mirza.e_kart.preferences.AppPreferences
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, RefreshProductListener {

    private val TAG = HomeActivity::class.java.simpleName
    private val fromStrings = arrayOf("productName")
    private var menuIndex = 0
    private var productList: ProductList? = null

    private var homeFragment: HomeFragment? = null
    private val appPreference by lazy {
        AppPreferences(this)
    }

    private val progressDialog by lazy {
        LoadingAlertDialog()
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpNavigationBar()
        getAllProducts()

        refresh.setOnClickListener {
            refresh.visibility = View.GONE
            getAllProducts()
        }
    }

    override fun onResume() {
        super.onResume()
        nav_view.menu.getItem(menuIndex).isChecked = true
    }

    private var isBackPressed = false
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (isBackPressed)
                super.onBackPressed()
            else {
                showToast("Press again to exit")
                isBackPressed = true
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
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
                performLogOut()
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
                query?.let {
                    val results = getMatchingItems(it)
                    if (results != null) {
                        Intent(this@HomeActivity, SearchResultActivity::class.java).apply {
                            putExtra("productList", results).also {
                                startActivity(it)
                            }
                        }

                    } else {
                        showAlert("No item found")
                    }
                }

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
        val headerView = nav_view.getHeaderView(0)

        setUserProfile(headerView)
    }


    private fun setUserProfile(view: View) {
        view.user_email.text = appPreference.getEmail()
        view.user_name.text = appPreference.getUserName()
        view.user_image.setImageResource(R.drawable.ic_person)
    }

    private fun moveToHomePage() {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_layout)
        if (fragment is HomeFragment)
            return
        supportFragmentManager.beginTransaction().replace(R.id.main_layout, homeFragment!!, "home_fragment").commit()
        menuIndex = 0
    }

    private fun moveToOrdersPage() {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_layout)
        if (fragment is OrderHistoryFragment)
            return
        supportFragmentManager.beginTransaction().replace(R.id.main_layout, OrderHistoryFragment(), "home_fragment")
            .commit()
        menuIndex = 1
    }

    private fun moveToReferralPage() {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_layout)
        if (fragment is ReferralFragment)
            return
        supportFragmentManager.beginTransaction().replace(R.id.main_layout, ReferralFragment(), "home_fragment")
            .commit()
        menuIndex = 2
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

    private fun performLogOut() {
        val innerNavView = nav_view
        val dialog = CustomAlertDialog().apply {
            arguments = Bundle().apply {
                setMessage("Are you sure you want to log out?")
                setSingleButton(false)
                setDismissListener(object : CustomDialogListener {
                    override fun onPositiveClicked() {
                        appPreference.deleteAll()
                        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                        finishAffinity()
                    }

                    override fun onNegativeClicked() {
                        innerNavView.menu.getItem(menuIndex)?.isChecked = true
                    }

                })
            }
        }
        dialog.show(supportFragmentManager, "hi_how")

    }

    private fun loadProducts() {
        nav_view.visibility = View.VISIBLE
        main_content.visibility = View.VISIBLE
        homeFragment = HomeFragment().apply {
            arguments = Bundle().apply {
                putParcelable("productList", productList)
            }
            setRefreshingListener(this@HomeActivity)
        }
        moveToHomePage()

    }


    private fun getAllProducts(isFromHome: Boolean = true) {
        refresh.visibility = View.GONE
        if (!isNetworkAvailable()) {
            if (homeFragment == null) {
                refresh.visibility = View.VISIBLE
                refresh.bringToFront()
            }
            homeFragment?.stopRefreshing()
            val dialog = CustomAlertDialog().apply {
                setMessage("Please check your internet.")
                setIcon(R.drawable.ic_warning)
                setSingleButton(true)
            }
            dialog.show(supportFragmentManager, "select_day_alert")

            return
        }
//        if (isFromHome)
        progressDialog.show(supportFragmentManager, "loading_alert_dailog")
        val call = ClientAPI.clientAPI.getProducts("Bearer " + appPreference.getJWTToken())
        Log.d(TAG, "Request URL : ${call.request().url()}")
        call.enqueue(object : Callback<ProductList> {
            override fun onResponse(call: Call<ProductList>, response: Response<ProductList>) {
//                if (isFromHome)
                hideAlert()
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse == null) {
                        showToast("Please try after sometime")
                        return
                    }
                    productList = productResponse
                    suggestionList.clear()
                    productList?.product?.forEach {
                        suggestionList.add(it.name)
                    }
                    loadProducts()
                } else {
                    if (homeFragment == null) {
                        refresh.visibility = View.VISIBLE
                        refresh.bringToFront()
                    }
                    showToast("Internal server error, please try again")
                }

                Log.d(TAG, "Response Code : ${response.code()}")
            }

            override fun onFailure(call: Call<ProductList>, t: Throwable) {
                hideAlert()
                t.printStackTrace()
                showToast("Network Error!")
            }
        })
    }

    private fun hideAlert() {
        homeFragment?.stopRefreshing()
        if (progressDialog.dialog != null && progressDialog.dialog.isShowing) {
            progressDialog.dismiss()
        }
    }


    private fun getMatchingItems(queryText: String): ProductList? {
        val list = productList?.product?.filter { it.name.contains(queryText) }
        return if (list != null && list.isNotEmpty()) {
            ProductList(ArrayList<ProductModel>().apply {
                addAll(list)
            })
        } else {
            null
        }
    }

    private fun showAlert(message: String) {
        val dialog = CustomAlertDialog().apply {
            setMessage(message)
            setSingleButton(true)
        }
        dialog.show(supportFragmentManager, "validation_alert")
        return
    }

    override fun onReferesh() {
        getAllProducts()
    }
}
