package com.mirza.e_kart.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.mirza.e_kart.R
import com.mirza.e_kart.adapters.ProductListAdapter
import com.mirza.e_kart.classes.RecyclerItemClickListener
import com.mirza.e_kart.customdialogs.CustomAlertDialog
import com.mirza.e_kart.extensions.hideLoadingAlert
import com.mirza.e_kart.extensions.isNetworkAvailable
import com.mirza.e_kart.extensions.showLoadingAlert
import com.mirza.e_kart.extensions.showToast
import com.mirza.e_kart.networks.ClientAPI
import com.mirza.e_kart.networks.models.BrandsModel
import com.mirza.e_kart.networks.models.Category
import com.mirza.e_kart.networks.models.ProductList
import com.mirza.e_kart.networks.models.ProductModel
import com.mirza.e_kart.preferences.AppPreferences
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.android.synthetic.main.search_result_contents.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchResultActivity : AppCompatActivity() {

    private val TAG = SearchResultActivity::class.java.simpleName
    private val productList by lazy {
        intent.getParcelableExtra<ProductList>("productList")?.product
    }
    private val productAdapter by lazy {
        search_results.adapter as ProductListAdapter
    }

    private val isCategory by lazy {
        intent.getBooleanExtra("isCat", false)
    }

    private val category by lazy {
        categoryModel?.name
    }

    private val categoryModel by lazy {
        intent.getParcelableExtra("cat") as Category?
    }

    private var brandList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        if (isCategory) {
            bar_title.text = category!!.capitalize()
            brand_filter.visibility = View.VISIBLE
            imageView3.visibility = View.VISIBLE
            getBrands()
        } else {
            brand_filter.visibility = View.GONE
            imageView3.visibility = View.GONE
        }
        setListeners()
        productList?.let {
            setProductList(it)

        }
    }


    private fun setBrandSpinner(brands: List<String>?) {
        brandList = ArrayList<String>().apply {
            add("Select Brand")
            add("All Brands")
            if (brands != null)
                addAll(brands)
        }
        val dataAdapter = ArrayAdapter<String>(
            this,
            R.layout.custom_spinner_item, brandList
        )
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner_item)
        brand_filter.adapter = dataAdapter
        brand_filter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Log.d(TAG, "onItemSelected called : $position ")
                (parent.getChildAt(0) as TextView).setTextColor(Color.parseColor("#FFFFFF"))
                setAdapterByBrand(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        imageView3.setOnClickListener {
            brand_filter.requestFocus()
            brand_filter.performClick()
        }
    }

    private fun setProductList(products: List<ProductModel>) {
        with(search_results) {
            layoutManager = android.support.v7.widget.LinearLayoutManager(context)
            adapter = com.mirza.e_kart.adapters.ProductListAdapter(ArrayList<ProductModel>().apply { addAll(products) })

            addOnItemTouchListener(
                com.mirza.e_kart.classes.RecyclerItemClickListener(
                    context,
                    this,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            android.util.Log.d(TAG, "Product at $position")
                            startProductDetailsPage(productAdapter.dataArray[position])

                        }

                    })
            )
        }
    }

    private fun startProductDetailsPage(productDetails: ProductModel) {
        Intent(this, ProductDetailsActivity::class.java).apply {
            putExtra("productDetails", productDetails)
        }.also {
            startActivity(it)
        }
    }


    private fun setListeners() {
        back_button.setOnClickListener {
            finish()
        }
    }

    private fun setAdapterByBrand(position: Int) {
        if (position == 0)
            return
        if (position == 1) {
            productList?.let {
                setProductList(it)
            }
            return
        }
        productList?.filter { it.brand_name == brandList[position] }?.let {
            setProductList(it)
        }
    }

    private fun getBrands() {
        if (!isNetworkAvailable()) {
            val dialog = CustomAlertDialog().apply {
                setMessage("Please check your internet.")
                setIcon(R.drawable.ic_warning)
                setSingleButton(true)
            }
            dialog.show(supportFragmentManager, "select_day_alert")
            setBrandSpinner(null)
            return
        }
        showLoadingAlert()
        val call = ClientAPI.clientAPI.getBrands("Bearer " + AppPreferences(this).getJWTToken(), categoryModel!!.id)
        Log.d(TAG, "Request URL : ${call.request().url()}")
        call.enqueue(object : Callback<BrandsModel> {
            override fun onResponse(call: Call<BrandsModel>, response: Response<BrandsModel>) {
                hideLoadingAlert()
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse == null) {
                        showToast("ServerError")
                        return
                    }
                    setBrandSpinner(productResponse.brands)
                } else {
                    setBrandSpinner(null)
                    showToast("Internal server error, please try again")
                }

                Log.d(TAG, "Response Code : ${response.code()}")
            }

            override fun onFailure(call: Call<BrandsModel>, t: Throwable) {
                hideLoadingAlert()
                setBrandSpinner(null)
                t.printStackTrace()
                showToast("Network Error!")
            }
        })
    }
}
