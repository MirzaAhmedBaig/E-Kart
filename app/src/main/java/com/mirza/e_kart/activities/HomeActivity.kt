package com.mirza.e_kart.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.support.design.widget.NavigationView
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
import com.mirza.e_kart.extensions.*
import com.mirza.e_kart.fragments.HomeFragment
import com.mirza.e_kart.listeners.CustomDialogListener
import com.mirza.e_kart.listeners.RefreshProductListener
import com.mirza.e_kart.networks.ClientAPI
import com.mirza.e_kart.networks.models.CategoriesModel
import com.mirza.e_kart.networks.models.ProductList
import com.mirza.e_kart.preferences.AppPreferences
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, RefreshProductListener {

    private val TAG = HomeActivity::class.java.simpleName
    val titles = listOf("Home", "My Orders", "Referral", "Feedback", "Contacts")
    private val fromStrings = arrayOf("productName")
    var menuIndex = 0
    private var productList: ProductList? = null
    private var categories: CategoriesModel? = null

    var homeFragment: HomeFragment? = null
    private val appPreference by lazy {
        AppPreferences(this)
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
        getCategories()

        refresh.setOnClickListener {
            refresh.visibility = View.GONE
            getCategories()
        }
    }

    override fun onResume() {
        super.onResume()
        nav_view.menu.getItem(menuIndex).isChecked = true
        toolbar.title = titles[menuIndex]
        isBackPressed = false
    }

    override fun onPause() {
        super.onPause()
        isBackPressed = false
    }

    private var isBackPressed = false
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else if (menuIndex != 0) {
            moveToHomePage()
            nav_view.menu.getItem(menuIndex).isChecked = true
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
            R.id.nav_feedback -> {
                moveToFeedback()
            }
            R.id.nav_contacts -> {
                moveToContactsPage()
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
                    val results = getMatchingItems(productList, it)
                    if (results != null) {
                        Intent(this@HomeActivity, SearchResultActivity::class.java).apply {
                            putExtra("productList", results)
                        }.also {
                            startActivity(it)
                        }

                    } else {
                        showAlert("No item found")
                    }
                }

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
            if (it.toLowerCase().contains(query.toLowerCase())) {
                cursor.addRow(arrayOf(index, it))
            }
        }
        suggestionAdapter.changeCursor(cursor)
    }

    private fun setUpNavigationBar() {
        toolbar.title = "Home"
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
                putParcelable("categories", categories)
            }
            setRefreshingListener(this@HomeActivity)
        }
        moveToHomePage()

    }

    private fun getAllProducts() {
        refresh.visibility = View.GONE
        if (!isNetworkAvailable()) {
            if (homeFragment == null) {
                refresh.visibility = View.VISIBLE
                refresh.bringToFront()
            }
            val dialog = CustomAlertDialog().apply {
                setMessage("Please check your internet.")
                setIcon(R.drawable.ic_warning)
                setSingleButton(true)
            }
            dialog.show(supportFragmentManager, "select_day_alert")

            return
        }
        showLoadingAlert()
        val call = ClientAPI.clientAPI.getProducts("Bearer " + appPreference.getJWTToken())
        Log.d(TAG, "Request URL : ${call.request().url()}")
        call.enqueue(object : Callback<ProductList> {
            override fun onResponse(call: Call<ProductList>, response: Response<ProductList>) {
                hideLoadingAlert()
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
                hideLoadingAlert()
                t.printStackTrace()
                showToast("Network Error!")
            }
        })
    }


    private fun getCategories() {
        refresh.visibility = View.GONE
        if (!isNetworkAvailable()) {
            if (homeFragment == null) {
                refresh.visibility = View.VISIBLE
                refresh.bringToFront()
            }
            val dialog = CustomAlertDialog().apply {
                setMessage("Please check your internet.")
                setIcon(R.drawable.ic_warning)
                setSingleButton(true)
            }
            dialog.show(supportFragmentManager, "select_day_alert")

            return
        }
        showLoadingAlert()
        val call = ClientAPI.clientAPI.getCategories("Bearer " + appPreference.getJWTToken())
        Log.d(TAG, "Request URL : ${call.request().url()}")
        call.enqueue(object : Callback<CategoriesModel> {
            override fun onResponse(call: Call<CategoriesModel>, response: Response<CategoriesModel>) {
                hideLoadingAlert()
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse == null) {
                        showToast("Please try after sometime")
                        return
                    }
                    Log.d(TAG, "Response Code : ${response.body()?.category?.size}")
                    categories = response.body()
                    getAllProducts()
                } else {
                    if (homeFragment == null) {
                        refresh.visibility = View.VISIBLE
                        refresh.bringToFront()
                    }
                    showToast("Internal server error, please try again")
                }

                Log.d(TAG, "Response Code : ${response.code()}")
            }

            override fun onFailure(call: Call<CategoriesModel>, t: Throwable) {
                hideLoadingAlert()
                t.printStackTrace()
                showToast("Network Error!")
            }
        })
    }

    override fun onReferesh() {
        getAllProducts()
    }
}
