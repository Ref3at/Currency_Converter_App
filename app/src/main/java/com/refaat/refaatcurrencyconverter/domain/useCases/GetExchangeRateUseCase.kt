package com.refaat.refaatcurrencyconverter.domain.useCases

import com.refaat.refaatcurrencyconverter.common.Resource
import com.refaat.refaatcurrencyconverter.domain.repository.CurrencyConverterRepository
import kotlinx.coroutines.flow.Flow

class GetExchangeRateUseCase(private val currencyConverterRepository: CurrencyConverterRepository) {
    operator fun invoke(
        from: String,
        to: String,
        startDate: String,
        endDate: String
    ): Flow<Resource<HashMap<String, HashMap<String, Double>>>> {
        return currencyConverterRepository.getExchangeRate(from, to, startDate, endDate)
    }
}