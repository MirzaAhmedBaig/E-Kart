package com.mirza.e_kart.networks

import com.mirza.e_kart.networks.models.LoginModel
import com.mirza.e_kart.networks.models.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface NetworkManager {
    /*@POST(MConfig.SIGN_UP)
    fun doSignUp(@Body userSignup: Any): Call<SignUpResponse>*/

    @Headers("Content-Type: application/json")
    @POST(MConfig.LOGIN)
    fun doLogin(@Body loginData: LoginModel): Call<LoginResponse>
}
