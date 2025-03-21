package com.markorkenyi.currencyConverter.network

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApi {
    // For fetching rates (if needed later)
    @GET("{baseCurrency}.json")
    suspend fun getRates(
        @Path("baseCurrency") baseCurrency: String
    ): CurrencyResponse

    // For fetching the list of available currencies
    @GET("currencies.json")
    suspend fun getCurrencies(): Map<String, String>
}
