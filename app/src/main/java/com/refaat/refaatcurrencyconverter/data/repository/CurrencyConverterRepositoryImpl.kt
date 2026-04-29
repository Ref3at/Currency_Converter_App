package com.refaat.refaatcurrencyconverter.data.repository

import com.refaat.refaatcurrencyconverter.common.Resource
import com.refaat.refaatcurrencyconverter.common.FALLBACK_FROM_CODE
import com.refaat.refaatcurrencyconverter.common.FALLBACK_TO_CODE
import com.refaat.refaatcurrencyconverter.data.CurrenciesDao
import com.refaat.refaatcurrencyconverter.data.remoteDataSource.CurrencyConverterAPI
import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem
import com.refaat.refaatcurrencyconverter.domain.model.FrankfurterHistoryResponse
import com.refaat.refaatcurrencyconverter.domain.repository.CurrencyConverterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class CurrencyConverterRepositoryImpl(
    private val dao: CurrenciesDao,
    private val api: CurrencyConverterAPI
) : CurrencyConverterRepository {

    override fun getCurrencies(): Flow<Resource<List<CurrencyItem>>> = flow {
        emit(Resource.Loading())

        val cached = dao.getCurrenciesList()
        if (cached.isNotEmpty()) {
            emit(Resource.Success(cached))
            return@flow
        }

        try {
            val remote = api.getCurrenciesList()
            val items = remote.map { (code, name) -> CurrencyItem(code, name) }
            dao.insertCurrenciesList(items)
            emit(Resource.Success(dao.getCurrenciesList()))
        } catch (e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!", cached))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server, check your internet connection.", cached))
        }
    }

    override fun getExchangeRate(
        from: String,
        to: String,
        startDate: String,
        endDate: String
    ): Flow<Resource<FrankfurterHistoryResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getExchangeRate(startDate, endDate, from, to)
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server, check your internet connection."))
        }
    }

    override suspend fun getDefaultCurrencies(countryCode: String): Pair<CurrencyItem, CurrencyItem> {
        val fromCode = countryToCurrencyCode[countryCode.uppercase()] ?: FALLBACK_FROM_CODE
        val toCode = if (fromCode == FALLBACK_FROM_CODE) FALLBACK_TO_CODE else FALLBACK_FROM_CODE

        val from = dao.getCurrency(fromCode) ?: CurrencyItem(fromCode, fromCode)
        val to = dao.getCurrency(toCode) ?: CurrencyItem(toCode, toCode)
        return Pair(from, to)
    }

    companion object {
        val countryToCurrencyCode = mapOf(
            "AU" to "AUD", "BG" to "BGN", "BR" to "BRL", "CA" to "CAD",
            "CH" to "CHF", "CN" to "CNY", "CZ" to "CZK", "DK" to "DKK",
            "GB" to "GBP", "HK" to "HKD", "HU" to "HUF", "ID" to "IDR",
            "IL" to "ILS", "IN" to "INR", "IS" to "ISK", "JP" to "JPY",
            "KR" to "KRW", "MX" to "MXN", "MY" to "MYR", "NO" to "NOK",
            "NZ" to "NZD", "PH" to "PHP", "PL" to "PLN", "RO" to "RON",
            "SE" to "SEK", "SG" to "SGD", "TH" to "THB", "TR" to "TRY",
            "US" to "USD", "ZA" to "ZAR",
            // Euro-zone countries
            "AT" to "EUR", "BE" to "EUR", "CY" to "EUR", "DE" to "EUR",
            "EE" to "EUR", "ES" to "EUR", "FI" to "EUR", "FR" to "EUR",
            "GR" to "EUR", "HR" to "EUR", "IE" to "EUR", "IT" to "EUR",
            "LT" to "EUR", "LU" to "EUR", "LV" to "EUR", "MT" to "EUR",
            "NL" to "EUR", "PT" to "EUR", "SI" to "EUR", "SK" to "EUR"
        )
    }
}
