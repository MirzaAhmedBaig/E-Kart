package com.mirza.e_kart.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.mirza.e_kart.R
import com.mirza.e_kart.adapters.ProductListAdapter
import com.mirza.e_kart.classes.RecyclerItemClickListener
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        setListeners()
        productList?.let {
            setProductList(it)

        }
    }

    private fun setProductList(products: ArrayList<ProductModel>) {
        with(search_results) {
            layoutManager = android.support.v7.widget.LinearLayoutManager(context)
            adapter = com.mirza.e_kart.adapters.ProductListAdapter(products)

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
}
