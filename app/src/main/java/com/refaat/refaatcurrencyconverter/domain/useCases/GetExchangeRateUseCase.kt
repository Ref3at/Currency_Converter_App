package com.refaat.refaatcurrencyconverter.domain.useCases

import com.refaat.refaatcurrencyconverter.common.Resource
import com.refaat.refaatcurrencyconverter.domain.model.FrankfurterHistoryResponse
import com.refaat.refaatcurrencyconverter.domain.repository.CurrencyConverterRepository
import kotlinx.coroutines.flow.Flow

class GetExchangeRateUseCase(private val repository: CurrencyConverterRepository) {
    operator fun invoke(
        from: String,
        to: String,
        startDate: String,
        endDate: String
    ): Flow<Resource<FrankfurterHistoryResponse>> {
        return repository.getExchangeRate(from, to, startDate, endDate)
    }
}
