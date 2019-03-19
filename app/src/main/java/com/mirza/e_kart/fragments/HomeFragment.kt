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
import com.mirza.e_kart.networks.models.ProductModel
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private val TAG = HomeFragment::class.java.simpleName
    private val productAdapter by lazy {
        product_list.adapter as ProductListAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProductList()
    }


    private fun setProductList() {
        with(product_list) {
            layoutManager = LinearLayoutManager(context)
            adapter = ProductListAdapter(dummyProducts)

            addOnItemTouchListener(
                RecyclerItemClickListener(
                    context,
                    this,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            Log.d(TAG, "Product at $position")
                            startProductDetailsPage(productAdapter.dataArray[position].id)

                        }

                    })
            )
        }


    }

    private fun startProductDetailsPage(productId: String) {
        Intent(context, ProductDetailsActivity::class.java).apply {
            putExtra("id", productId)
        }.also {
            context?.startActivity(it)
        }
    }

    private val dummyProducts = ArrayList<ProductModel>().apply {
        (0..5).forEach {
            add(
                ProductModel(
                    "$it", "$it Product", "$it Category", "$it brand",
                    "https://rukminim1.flixcart.com/image/832/832/jl16s280/mobile/t/v/q/nokia-6-1-plus-na-original-imaf892edytmjfgg.jpeg?q=70",
                    "4 GB RAM | 64 GB ROM | Expandable Upto 400 GB", "2", "$it 500", "", ""
                )
            )
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
