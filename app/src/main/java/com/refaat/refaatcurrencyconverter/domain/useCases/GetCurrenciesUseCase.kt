package com.refaat.refaatcurrencyconverter.domain.useCases

import com.refaat.refaatcurrencyconverter.common.Resource
import com.refaat.refaatcurrencyconverter.domain.repository.CurrencyConverterRepository
import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem
import kotlinx.coroutines.flow.Flow

class GetCurrenciesUseCase(private val currencyConverterRepository: CurrencyConverterRepository) {
    operator fun invoke(): Flow<Resource<List<CurrencyItem>>> {
        return currencyConverterRepository.getCurrencies()
    }
}