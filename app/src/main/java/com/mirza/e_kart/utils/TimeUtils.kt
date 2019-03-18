package com.mirza.e_kart.utils

import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Mirza Ahmed Baig on 15/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */

fun timestampToDate(s: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM/dd/yyyy")
        val netDate = Date(s)
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}

fun stringDateToTimestamp(date: String): Long {
    val sdf = SimpleDateFormat("MMM/dd/yyyy")
    val netDate = sdf.parse(date)
    //  //Log.d(TAG, "Converted Timestamp : ${netDate.time}")
    return netDate.time
}

fun compareTimestamp(date1: Long, date2: Long): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()
    cal1.time = Date(date1)
    cal2.time = Date(date2)
    //   //Log.d("TAG", "DAY_OF_YEAR : ${cal2.get(Calendar.DAY_OF_YEAR)}\nDAY_OF_MONTH : ${cal2.get(Calendar.DAY_OF_MONTH)}")
    return if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
        cal2.get(Calendar.DAY_OF_YEAR) - cal1.get(Calendar.DAY_OF_YEAR) < 2
    } else {
        return if ((cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR)) == 1) {
            cal1.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.DAY_OF_YEAR) == 364
        } else {
            false
        }
    }
}


fun isEqualDate(date1: Long, date2: Long): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()
    cal1.time = Date(date1)
    cal2.time = Date(date2)
    // //Log.d(TAG, "DAY 1 : ${cal1.get(Calendar.DAY_OF_YEAR)}  DAY 2 : ${cal2.get(Calendar.DAY_OF_YEAR)}")
    // //Log.d(TAG, "isEqualDate called : ${(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) && (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR))}")
    return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) && (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR))
}

fun getDateDifference(date1: Long, date2: Long): Int {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()
    cal1.time = Date(date1)
    cal2.time = Date(date2)
    return (cal2.get(Calendar.DAY_OF_YEAR) - cal1.get(Calendar.DAY_OF_YEAR))
}