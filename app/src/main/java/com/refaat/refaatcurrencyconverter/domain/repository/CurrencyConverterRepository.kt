package com.refaat.refaatcurrencyconverter.domain.repository

import com.refaat.refaatcurrencyconverter.common.Resource
import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem
import com.refaat.refaatcurrencyconverter.domain.model.FrankfurterHistoryResponse
import kotlinx.coroutines.flow.Flow

interface CurrencyConverterRepository {

    fun getCurrencies(): Flow<Resource<List<CurrencyItem>>>

    fun getExchangeRate(
        from: String,
        to: String,
        startDate: String,
        endDate: String
    ): Flow<Resource<FrankfurterHistoryResponse>>

    suspend fun getDefaultCurrencies(countryCode: String): Pair<CurrencyItem, CurrencyItem>
}
