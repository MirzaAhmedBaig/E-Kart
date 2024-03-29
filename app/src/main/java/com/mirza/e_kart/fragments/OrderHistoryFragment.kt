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
import com.mirza.e_kart.activities.OrderDetailsActivity
import com.mirza.e_kart.adapters.OrderListAdapter
import com.mirza.e_kart.classes.RecyclerItemClickListener
import com.mirza.e_kart.customdialogs.CustomAlertDialog
import com.mirza.e_kart.networks.ClientAPI
import com.mirza.e_kart.networks.models.OrderHistoryModel
import com.mirza.e_kart.networks.models.OrderedProduct
import com.mirza.e_kart.preferences.AppPreferences
import hideLoadingAlert
import isNetworkAvailable
import kotlinx.android.synthetic.main.fragment_order_history.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import showLoadingAlert
import showToast

class OrderHistoryFragment : Fragment() {

    private val TAG = OrderHistoryFragment::class.java.simpleName
    private val ordersAdapter by lazy {
        orders_list.adapter as OrderListAdapter
    }

    private val appPreferences by lazy {
        AppPreferences(context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getOrderHistory()
    }

    private fun setOrdersList(list: ArrayList<OrderedProduct>) {
        with(orders_list) {
            layoutManager = LinearLayoutManager(context)
            adapter = OrderListAdapter(list)


            addOnItemTouchListener(
                RecyclerItemClickListener(
                    context,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            Log.d(TAG, "Product at $position")
                            startProductDetailsPage(ordersAdapter.dataArray[position])
                        }

                    })
            )
        }
        if (list.isNotEmpty()) {
            no_orders_text.visibility = View.GONE
        }
    }

    private fun startProductDetailsPage(orderDetails: OrderedProduct) {
        Intent(context, OrderDetailsActivity::class.java).apply {
            putExtra("productDetails", orderDetails)
        }.also {
            context?.startActivity(it)
        }
    }

    private fun getOrderHistory() {
        if (!isNetworkAvailable()) {
            val dialog = CustomAlertDialog().apply {
                setMessage("Please check your internet.")
                setIcon(R.drawable.ic_warning)
                setSingleButton(true)
            }
            dialog.show(fragmentManager, "select_day_alert")
            return
        }
        showLoadingAlert()
        val call =
            ClientAPI.clientAPI.getOrderHistory("Bearer " + appPreferences.getJWTToken(), appPreferences.getUser().id)
        Log.d(TAG, "Request URL : ${call.request().url()} , ID : ${appPreferences.getUser().id}")
        call.enqueue(object : Callback<OrderHistoryModel> {
            override fun onResponse(call: Call<OrderHistoryModel>, response: Response<OrderHistoryModel>) {
                hideLoadingAlert()
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse == null) {
                        showToast("NO orders found")
                        return
                    }
                    Log.d(TAG, "Response ${productResponse.product}")
                    Log.d(TAG, "Size ${productResponse.product.size}")
                    setOrdersList(productResponse.product)

                } else {
                    showToast("Internal server error, please try again")
                }

                Log.d(TAG, "Response Code : ${response.code()} ${response.isSuccessful}")
            }

            override fun onFailure(call: Call<OrderHistoryModel>, t: Throwable) {
                hideLoadingAlert()
                t.printStackTrace()
                showToast("Network Error!")
            }
        })
    }
}
