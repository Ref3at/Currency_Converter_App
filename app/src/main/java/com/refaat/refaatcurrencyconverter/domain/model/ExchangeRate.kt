package com.refaat.refaatcurrencyconverter.domain.model

data class ExchangeRate(
    val currencyFrom: String,
    val currencyTo: String,
    val todayRate: Double,
    val last7DaysRates: List<Pair<String, Double>>
)
