package com.refaat.refaatcurrencyconverter.ui.currencyConversion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refaat.refaatcurrencyconverter.common.Resource
import com.refaat.refaatcurrencyconverter.common.sdf
import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem
import com.refaat.refaatcurrencyconverter.domain.model.ExchangeRate
import com.refaat.refaatcurrencyconverter.domain.model.FrankfurterHistoryResponse
import com.refaat.refaatcurrencyconverter.domain.useCases.UseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CurrencyConversionViewModel @Inject constructor(
    private val useCases: UseCases
) : ViewModel() {

    var currentRate = MutableLiveData(1.0)
    val currenciesLiveData: MutableLiveData<Resource<List<CurrencyItem>>> = MutableLiveData()
    val fromCurrency: MutableLiveData<CurrencyItem> = MutableLiveData()
    val toCurrency: MutableLiveData<CurrencyItem> = MutableLiveData()
    val exchangeRate: MutableLiveData<Resource<ExchangeRate>> = MutableLiveData()
    val lastUpdated: MutableLiveData<String> = MutableLiveData()

    init {
        viewModelScope.launch {
            useCases.getCurrenciesUseCase().collect {
                currenciesLiveData.value = it
                if (it is Resource.Success) {
                    val defaults = useCases.getDefaultCurrenciesUseCase()
                    fromCurrency.value = defaults.first
                    toCurrency.value = defaults.second
                }
            }
        }
    }

    fun getTodayDate(): String = sdf.format(Date())

    private fun getFrom8DaysDate(): String {
        val cal = GregorianCalendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -7)
        return sdf.format(cal.time)
    }

    fun getExchangeDate() {
        val from = fromCurrency.value ?: return
        val to = toCurrency.value ?: return

        viewModelScope.launch {
            useCases.getExchangeRateUseCase(
                from.currencyCode,
                to.currencyCode,
                getFrom8DaysDate(),
                getTodayDate()
            ).collect {
                when (it) {
                    is Resource.Loading -> exchangeRate.value = Resource.Loading()
                    is Resource.Error -> exchangeRate.value = Resource.Error(it.message)
                    is Resource.Success -> {
                        exchangeRate.value = Resource.Success(parseExchangeRate(it.data))
                        lastUpdated.value = sdf.format(Date())
                    }
                }
            }
        }
    }

    fun swapCurrencies() {
        val temp = fromCurrency.value
        fromCurrency.value = toCurrency.value
        toCurrency.value = temp
    }

    private fun parseExchangeRate(response: FrankfurterHistoryResponse?): ExchangeRate? {
        if (response == null) return null

        // rates map: "2024-01-08" -> {"EUR": 0.91}
        val sortedDates = response.rates.keys.sortedDescending()
        if (sortedDates.isEmpty()) return null

        val todayValue = response.rates[sortedDates[0]]?.values?.firstOrNull() ?: return null
        currentRate.value = todayValue

        val historyDates = if (sortedDates.size > 1) sortedDates.subList(1, sortedDates.size) else emptyList()
        val pairs = historyDates.mapNotNull { date ->
            val rate = response.rates[date]?.values?.firstOrNull() ?: return@mapNotNull null
            Pair(date, rate)
        }

        return ExchangeRate(
            currencyFrom = response.base,
            currencyTo = response.rates.values.firstOrNull()?.keys?.firstOrNull() ?: "",
            todayRate = todayValue,
            last7DaysRates = pairs
        )
    }
}
