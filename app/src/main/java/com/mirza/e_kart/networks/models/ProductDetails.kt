package com.mirza.e_kart.networks.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ProductDetails : RealmObject() {
    @PrimaryKey
    var id: Int = -1
    var name: String = ""
    var category_id: Int = -1
    var brand_id: Int = -1
    var image: String = ""
    var description: String = ""
    var interest: Float = 0f
    var price: Int = 0
    var created_at: Long = 0
    var updated_at: Long = 0
}
