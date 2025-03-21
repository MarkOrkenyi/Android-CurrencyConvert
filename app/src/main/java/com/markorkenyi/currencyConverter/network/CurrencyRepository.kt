package com.markorkenyi.currencyConverter.network

class CurrencyRepository {
    suspend fun getCurrencies(): Map<String, String> {
        return RetrofitInstance.api.getCurrencies()
    }

    suspend fun getRates(baseCurrency: String): CurrencyResponse {
        return RetrofitInstance.api.getRates(baseCurrency.lowercase())
    }
}
