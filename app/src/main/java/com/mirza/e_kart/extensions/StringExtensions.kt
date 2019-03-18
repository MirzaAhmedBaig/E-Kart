package com.mirza.e_kart.extensions

import java.util.regex.Pattern


/**
 * Created by Mirza Ahmed Baig on 15/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */

fun String.isEmailValid(): Boolean {
    val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]{2}+[a-z]*")
    return emailPattern.matcher(this.trim()).matches() && this.trim().isNotEmpty()
}