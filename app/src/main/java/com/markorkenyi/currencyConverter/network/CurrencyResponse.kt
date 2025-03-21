package com.markorkenyi.currencyConverter.network

data class CurrencyResponse(
    val date: String,
    val rates: Map<String, Double>
)
