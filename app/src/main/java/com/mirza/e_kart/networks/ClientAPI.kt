package com.mirza.e_kart.networks


import com.google.gson.GsonBuilder
import com.mirza.e_kart.networks.MConfig.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by MIRZA on 16/09/17.
 */

object ClientAPI {
    var retrofit: Retrofit? = null
        private set
    private var networkManager: NetworkManager? = null

    val clientAPI: NetworkManager
        get() {
            if (networkManager == null) {
                val gson = GsonBuilder()
                    .setLenient()
                    .create()
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                networkManager = retrofit!!.create(NetworkManager::class.java)
            }
            return networkManager!!
        }
}