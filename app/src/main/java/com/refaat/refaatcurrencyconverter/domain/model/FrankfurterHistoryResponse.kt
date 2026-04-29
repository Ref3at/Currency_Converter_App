package com.refaat.refaatcurrencyconverter.domain.model

data class FrankfurterHistoryResponse(
    val amount: Double,
    val base: String,
    val start_date: String,
    val end_date: String,
    val rates: HashMap<String, HashMap<String, Double>>
)
