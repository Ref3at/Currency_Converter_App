package com.refaat.refaatcurrencyconverter.common

import java.text.SimpleDateFormat

const val DECIMAL_PLACES = 4
const val GBR_CODE = "GB"
const val USD_CODE = "US"
const val COMPACT_TYPE = "ultra"
const val SELECTION_KEY = "selection_key"
const val SELECTED_CURRENCY = "selection_currency"
val sdf = SimpleDateFormat("yyyy-MM-dd")
val sdfDayName = SimpleDateFormat("EEE, d MMM")

fun getTheFlagURL(countryId: String?): String {
    return "https://flagcdn.com/w160/${countryId?.lowercase()}.png"
}

enum class TYPE { CURRENCY_FROM, CURRENCY_TO }
