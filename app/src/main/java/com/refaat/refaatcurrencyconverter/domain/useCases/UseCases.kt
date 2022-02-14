package com.refaat.refaatcurrencyconverter.domain.useCases

data class UseCases(
    val getCurrenciesUseCase: GetCurrenciesUseCase,
    val getExchangeRateUseCase: GetExchangeRateUseCase,
    val getDefaultCurrenciesUseCase: GetDefaultCurrenciesUseCase
)