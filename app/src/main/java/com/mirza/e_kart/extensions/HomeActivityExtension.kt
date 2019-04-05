package com.mirza.e_kart.extensions

import android.support.v7.app.AppCompatActivity
import com.mirza.e_kart.R
import com.mirza.e_kart.activities.HomeActivity
import com.mirza.e_kart.fragments.*
import com.mirza.e_kart.networks.models.ProductList
import com.mirza.e_kart.networks.models.ProductModel
import kotlinx.android.synthetic.main.app_bar_main.*


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
    toolbar.title = titles[menuIndex]
}

fun HomeActivity.moveToOrdersPage() {
    val fragment = supportFragmentManager.findFragmentById(R.id.main_layout)
    if (fragment is OrderHistoryFragment)
        return
    supportFragmentManager.beginTransaction().replace(R.id.main_layout, OrderHistoryFragment(), "home_fragment")
        .commit()
    menuIndex = 1
    toolbar.title = titles[menuIndex]
}

fun HomeActivity.moveToReferralPage() {
    val fragment = supportFragmentManager.findFragmentById(R.id.main_layout)
    if (fragment is ReferralFragment)
        return
    supportFragmentManager.beginTransaction().replace(R.id.main_layout, ReferralFragment(), "home_fragment")
        .commit()
    menuIndex = 2
    toolbar.title = titles[menuIndex]
}


fun HomeActivity.moveToFeedback() {
    val fragment = supportFragmentManager.findFragmentById(R.id.main_layout)
    if (fragment is FeedbackFragment)
        return
    supportFragmentManager.beginTransaction().replace(R.id.main_layout, FeedbackFragment(), "contacts_fragment")
        .commit()
    menuIndex = 3
    toolbar.title = titles[menuIndex]
}

fun HomeActivity.moveToContactsPage() {
    val fragment = supportFragmentManager.findFragmentById(R.id.main_layout)
    if (fragment is ContactFragment)
        return
    supportFragmentManager.beginTransaction().replace(R.id.main_layout, ContactFragment(), "contacts_fragment")
        .commit()
    menuIndex = 4
    toolbar.title = titles[menuIndex]
}

fun AppCompatActivity.getMatchingItems(productList: ProductList?, queryText: String): ProductList? {
    val list = productList?.product?.filter { it.name.contains(queryText, true) }
    return if (list != null && list.isNotEmpty()) {
        ProductList(ArrayList<ProductModel>().apply {
            addAll(list)
        })
    } else {
        null
    }
}