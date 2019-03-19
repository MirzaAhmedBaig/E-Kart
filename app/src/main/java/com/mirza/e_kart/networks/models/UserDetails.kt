package com.mirza.e_kart.networks.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserDetails : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var first_name: String = ""
    var last_name: String = ""
    var email: String = ""
    var mobile_number: String = ""
    var password: String = ""
    var referal_id: String = ""
    var reference_code: String = ""
    var created_at: Long = 0
    var updated_at: Long = 0
}