package com.mirza.e_kart.networks.models

import java.math.BigInteger

data class SignupModel(
    val first_name: String,
    val last_name: String,
    val email: String,
    val mobile_number: BigInteger,
    val password: String,
    val reference_code: String
)
