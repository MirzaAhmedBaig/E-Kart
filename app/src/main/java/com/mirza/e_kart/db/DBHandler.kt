/*
package com.mirza.e_kart.db

import com.mirza.e_kart.networks.models.UserDetails
import io.realm.Realm

private val TAG = "DBHandler"

fun storeUserDetails(realm: Realm, userDetails: UserDetails) {
    realm.executeTransaction {
        realm.copyToRealm(userDetails)
    }
}

fun storeProducts(realm: Realm) {

}

fun removeAll() {
    val realm = Realm.getDefaultInstance()
    realm.executeTransaction {
        realm.where(UserDetails::class.java).findAll().deleteAllFromRealm()
    }

}*/
