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
import com.mirza.e_kart.extensions.brandList
import com.mirza.e_kart.networks.models.ProductList
import com.mirza.e_kart.networks.models.ProductModel
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.android.synthetic.main.search_result_contents.*


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
        intent.getStringExtra("cat")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        if (isCategory) {
            bar_title.text = category.capitalize()
            brand_filter.visibility = View.VISIBLE
            imageView3.visibility = View.VISIBLE
            setBrandSpinner(brandList.distinct())
        } else {
            brand_filter.visibility = View.GONE
            imageView3.visibility = View.GONE
        }
        setListeners()
        productList?.let {
            setProductList(it)

        }
    }


    private fun setBrandSpinner(brands: List<String>) {
        val dataAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item, ArrayList<String>().apply {
                addAll(brands)
            }
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
}
