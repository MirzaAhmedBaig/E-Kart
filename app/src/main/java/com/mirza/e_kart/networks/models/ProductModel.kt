package com.mirza.e_kart.networks.models

data class ProductModel(
    val id: String,
    val name: String,
    val category_id: String,
    val brand_id: String,
    val image: String,
    val description: String,
    val interest: String,
    val price: String,
    val created_at: String,
    val updated_at: String
)