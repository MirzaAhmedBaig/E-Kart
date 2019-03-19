package com.mirza.e_kart.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mirza.e_kart.R
import com.mirza.e_kart.adapters.OrderListAdapter
import com.mirza.e_kart.networks.models.ProductModel
import kotlinx.android.synthetic.main.fragment_order_history.*

class OrderHistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOrdersList()
    }

    private fun setOrdersList() {
        with(orders_list) {
            layoutManager = LinearLayoutManager(context)
            adapter = OrderListAdapter(dummyProducts)
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderHistoryFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private val dummyProducts = ArrayList<ProductModel>().apply {
        (0..5).forEach {
            add(
                ProductModel(
                    it,
                    "$it Product",
                    it,
                    it,
                    "https://rukminim1.flixcart.com/image/832/832/jl16s280/mobile/t/v/q/nokia-6-1-plus-na-original-imaf892edytmjfgg.jpeg?q=70",
                    "4 GB RAM | 64 GB ROM | Expandable Upto 400 GB",
                    2f,
                    it * 1000,
                    System.currentTimeMillis(),
                    System.currentTimeMillis()
                )
            )
        }
    }
}
