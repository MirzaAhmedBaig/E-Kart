package com.mirza.e_kart.networks.models

data class ProductModel(
    val id: Int,
    val name: String,
    val category_id: Int,
    val brand_id: Int,
    val image: String,
    val description: String,
    val interest: Float,
    val price: Int,
    val created_at: Long,
    val updated_at: Long
)