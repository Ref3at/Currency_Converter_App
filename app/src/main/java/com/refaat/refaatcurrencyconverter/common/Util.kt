package com.refaat.refaatcurrencyconverter.common

import java.text.SimpleDateFormat
import java.util.Locale

const val DECIMAL_PLACES = 4
const val FALLBACK_FROM_CODE = "USD"
const val FALLBACK_TO_CODE = "EUR"
const val SELECTION_KEY = "selection_key"
const val SELECTED_CURRENCY = "selection_currency"

val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
val sdfDayName = SimpleDateFormat("EEE, d MMM", Locale.US)

// Maps ISO 4217 currency code → ISO 3166-1 alpha-2 country code for flagcdn.com
val currencyToCountryCode = mapOf(
    "AUD" to "au", "BGN" to "bg", "BRL" to "br", "CAD" to "ca",
    "CHF" to "ch", "CNY" to "cn", "CZK" to "cz", "DKK" to "dk",
    "EUR" to "eu", "GBP" to "gb", "HKD" to "hk", "HUF" to "hu",
    "IDR" to "id", "ILS" to "il", "INR" to "in", "ISK" to "is",
    "JPY" to "jp", "KRW" to "kr", "MXN" to "mx", "MYR" to "my",
    "NOK" to "no", "NZD" to "nz", "PHP" to "ph", "PLN" to "pl",
    "RON" to "ro", "SEK" to "se", "SGD" to "sg", "THB" to "th",
    "TRY" to "tr", "USD" to "us", "ZAR" to "za"
)

fun getTheFlagURL(currencyCode: String?): String {
    val countryCode = currencyToCountryCode[currencyCode] ?: return ""
    return "https://flagcdn.com/w160/$countryCode.png"
}

enum class TYPE { CURRENCY_FROM, CURRENCY_TO }
