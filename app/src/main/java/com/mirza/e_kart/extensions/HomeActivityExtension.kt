package com.mirza.e_kart.extensions

import android.support.v7.app.AppCompatActivity
import com.mirza.e_kart.R
import com.mirza.e_kart.activities.HomeActivity
import com.mirza.e_kart.fragments.HomeFragment
import com.mirza.e_kart.fragments.OrderHistoryFragment
import com.mirza.e_kart.fragments.ReferralFragment
import com.mirza.e_kart.networks.models.ProductList
import com.mirza.e_kart.networks.models.ProductModel


/**
 * Created by Mirza Ahmed Baig on 28/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */

fun HomeActivity.moveToHomePage() {
    val fragment = supportFragmentManager.findFragmentById(R.id.main_layout)
    if (fragment is HomeFragment)
        return
    supportFragmentManager.beginTransaction().replace(R.id.main_layout, homeFragment!!, "home_fragment").commit()
    menuIndex = 0
}

fun HomeActivity.moveToOrdersPage() {
    val fragment = supportFragmentManager.findFragmentById(R.id.main_layout)
    if (fragment is OrderHistoryFragment)
        return
    supportFragmentManager.beginTransaction().replace(R.id.main_layout, OrderHistoryFragment(), "home_fragment")
        .commit()
    menuIndex = 1
}

fun HomeActivity.moveToReferralPage() {
    val fragment = supportFragmentManager.findFragmentById(R.id.main_layout)
    if (fragment is ReferralFragment)
        return
    supportFragmentManager.beginTransaction().replace(R.id.main_layout, ReferralFragment(), "home_fragment")
        .commit()
    menuIndex = 2
}

fun AppCompatActivity.getMatchingItems(productList: ProductList?, queryText: String): ProductList? {
    val list = productList?.product?.filter { it.name.contains(queryText) }
    return if (list != null && list.isNotEmpty()) {
        ProductList(ArrayList<ProductModel>().apply {
            addAll(list)
        })
    } else {
        null
    }
}