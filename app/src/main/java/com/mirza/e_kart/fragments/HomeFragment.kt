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
import com.mirza.e_kart.adapters.ProductListAdapter
import com.mirza.e_kart.classes.RecyclerItemClickListener
import com.mirza.e_kart.listeners.RefreshProductListener
import com.mirza.e_kart.networks.models.ProductList
import com.mirza.e_kart.networks.models.ProductModel
import isNetworkAvailable
import kotlinx.android.synthetic.main.fragment_home.*
import showToast


class HomeFragment : Fragment() {

    private val TAG = HomeFragment::class.java.simpleName
    private var refreshProductListener: RefreshProductListener? = null
    private val productAdapter by lazy {
        product_list.adapter as ProductListAdapter
    }

    private val productList by lazy {
        (arguments?.getParcelable("productList") as ProductList?)?.product
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productList?.let {
            setProductList(it)

        }
        setRefreshLayout()
    }

    override fun onResume() {
        super.onResume()
        stopRefreshing()
    }

    private fun setRefreshLayout() {
        swipe_refresh_layout.setOnRefreshListener {
            stopRefreshing()
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

    fun stopRefreshing() {
        swipe_refresh_layout?.isRefreshing = false
    }
}
