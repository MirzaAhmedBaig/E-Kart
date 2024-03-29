package com.mirza.e_kart.extensions

import com.mirza.e_kart.networks.models.ProductList

val genderList = ArrayList<String>().apply {
    add("Gender")
    add("Male")
    add("Female")
    add("Other")
}

val pinCodes = ArrayList<String>().apply {
    add("Pin Code")
    (440001..440037).forEach {
        add(it.toString())
    }
    add(441001.toString())
    add(441108.toString())
    add(441111.toString())
    add(441123.toString())
    add(441204.toString())
    add(441501.toString())
}

val residences = ArrayList<String>().apply {
    add("Residence Type")
    add("Owned")
    add("Rental")
}

val employmentType = ArrayList<String>().apply {
    add("Employment Type")
    add("Self Employee")
    add("Salaried")
}

val suggestionList = ArrayList<String>()
var orderListBackup: ProductList? = null

