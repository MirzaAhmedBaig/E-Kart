package com.mirza.e_kart.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mirza.e_kart.R
import com.mirza.e_kart.activities.ProductDetailsActivity
import com.mirza.e_kart.activities.SearchResultActivity
import com.mirza.e_kart.adapters.CategoryAdapter
import com.mirza.e_kart.adapters.ProductListAdapter
import com.mirza.e_kart.classes.RecyclerItemClickListener
import com.mirza.e_kart.listeners.CategoryListener
import com.mirza.e_kart.listeners.RefreshProductListener
import com.mirza.e_kart.networks.models.CategoriesModel
import com.mirza.e_kart.networks.models.Category
import com.mirza.e_kart.networks.models.ProductList
import com.mirza.e_kart.networks.models.ProductModel
import getMatchingItemsByCategory
import isNetworkAvailable
import kotlinx.android.synthetic.main.fragment_home.*
import showAlert
import showToast


class HomeFragment : Fragment() {

    private val TAG = HomeFragment::class.java.simpleName
    private var refreshProductListener: RefreshProductListener? = null
    private var categoryListener: CategoryListener? = null
    private val productAdapter by lazy {
        product_list.adapter as ProductListAdapter
    }

    private val productList by lazy {
        (arguments?.getParcelable("productList") as ProductList?)?.product
    }

    private val categories by lazy {
        (arguments?.getParcelable("categories") as CategoriesModel?)?.category
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categories?.let {
            setCategoriesList(it)

        }
        productList?.let {
            setProductList(it)

        }
        setRefreshLayout()
    }

    override fun onResume() {
        super.onResume()
        swipe_refresh_layout?.isRefreshing = false
    }

    private fun setRefreshLayout() {
        swipe_refresh_layout.setOnRefreshListener {
            swipe_refresh_layout?.isRefreshing = false
            refreshProductListener?.onReferesh()
        }
    }

    private fun setProductList(products: ArrayList<ProductModel>) {
        with(product_list) {
            layoutManager = LinearLayoutManager(context)
            adapter = ProductListAdapter(products)

            addOnItemTouchListener(
                RecyclerItemClickListener(
                    context,
                    this,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            Log.d(TAG, "Product at $position")
                            if (isNetworkAvailable())
                                startProductDetailsPage(productAdapter.dataArray[position])
                            else
                                showToast("Internet is not available")

                        }

                    })
            )
        }
    }

    private fun setCategoriesList(categories: ArrayList<Category>) {
        with(category_list) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = CategoryAdapter(categories)

            addOnItemTouchListener(
                RecyclerItemClickListener(
                    context,
                    this,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            Log.d(TAG, "Product at $position")
                            productList?.let {
                                val results = getMatchingItemsByCategory(it, categories[position].id)
                                if (results != null) {
                                    Intent(context, SearchResultActivity::class.java).apply {
                                        putExtra("productList", results)
                                        putExtra("isCat", true)
                                        putExtra("cat", categories[position])
                                    }.also {
                                        startActivity(it)
                                    }

                                } else {
                                    showAlert("No item found")
                                }
                            } ?: showToast("No items found")
                        }

                    })
            )
        }
    }

    private fun startProductDetailsPage(productDetails: ProductModel) {
        Intent(context, ProductDetailsActivity::class.java).apply {
            putExtra("productDetails", productDetails)
        }.also {
            context?.startActivity(it)
        }
    }

    fun setRefreshingListener(refreshProductListener: RefreshProductListener) {
        this.refreshProductListener = refreshProductListener
    }
}
