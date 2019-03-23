package com.mirza.e_kart.networks


import com.google.gson.GsonBuilder
import com.mirza.e_kart.networks.MConfig.Companion.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Created by MIRZA on 16/09/17.
 */

object ClientAPI {
    var retrofit: Retrofit? = null
        private set
    private var networkManager: NetworkManager? = null

    var okHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(200, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .build()

    val clientAPI: NetworkManager
        get() {
            if (networkManager == null) {
                val gson = GsonBuilder()
                    .setLenient()
                    .create()
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                networkManager = retrofit!!.create(NetworkManager::class.java)
            }
            return networkManager!!
        }
}