package com.mirza.e_kart.networks.models


/**
 * Created by Mirza Ahmed Baig on 19/03/19.
 * Avantari Technologies
 * mirza@avantari.org
 */
data class LoginResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Long
)