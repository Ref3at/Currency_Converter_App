package com.refaat.refaatcurrencyconverter.domain.useCases

import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem
import com.refaat.refaatcurrencyconverter.domain.model.DeviceCode
import com.refaat.refaatcurrencyconverter.domain.repository.CurrencyConverterRepository

class GetDefaultCurrenciesUseCase(
    private val currencyConverterRepository: CurrencyConverterRepository,
    private val deviceCode: DeviceCode
) {
    suspend operator fun invoke(): Pair<CurrencyItem, CurrencyItem> {
        return currencyConverterRepository.getDefaultCurrencies(deviceCode.name)
    }
}