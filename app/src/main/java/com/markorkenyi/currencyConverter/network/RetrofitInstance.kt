package com.markorkenyi.currencyConverter.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder

object RetrofitInstance {
    private val gson = GsonBuilder().create()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val api: CurrencyApi by lazy {
        retrofit.create(CurrencyApi::class.java)
    }
}
