package com.refaat.refaatcurrencyconverter.ui.currencyConversion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refaat.refaatcurrencyconverter.common.Resource
import com.refaat.refaatcurrencyconverter.common.sdf
import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem
import com.refaat.refaatcurrencyconverter.domain.model.ExchangeRate
import com.refaat.refaatcurrencyconverter.domain.useCases.UseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class CurrencyConversionViewModel @Inject constructor(
    private val useCases: UseCases
) :
    ViewModel() {
    var currentRate = MutableLiveData(1.0)
    val currenciesLiveData: MutableLiveData<Resource<List<CurrencyItem>>> = MutableLiveData()
    val fromCurrency: MutableLiveData<CurrencyItem> = MutableLiveData()
    val toCurrency: MutableLiveData<CurrencyItem> = MutableLiveData()
    val exchangeRate: MutableLiveData<Resource<ExchangeRate>> = MutableLiveData()

    init {
        viewModelScope.launch {
            useCases.getCurrenciesUseCase().collect {
                currenciesLiveData.value = it
                if (it is Resource.Success) {
                    val defaultCurrencies = useCases.getDefaultCurrenciesUseCase()
                    fromCurrency.value = defaultCurrencies.first
                    toCurrency.value = defaultCurrencies.second
                }
            }
        }
    }

    fun getTodayDate(): String {
        val cal: Calendar = GregorianCalendar.getInstance()
        cal.time = Date()
        return sdf.format(Date().time)
    }

    private fun getFrom8DaysDate(): String {
        val cal: Calendar = GregorianCalendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DAY_OF_YEAR, -7);
        return sdf.format(cal.time)
    }

    fun getExchangeDate() {

        if (fromCurrency.value == null)
            return
        if (toCurrency.value == null)
            return

        viewModelScope.launch {
            useCases.getExchangeRateUseCase(
                fromCurrency.value!!.currencyId!!,
                toCurrency.value!!.currencyId!!, getFrom8DaysDate(), getTodayDate()
            ).collect {

                when (it) {
                    is Resource.Loading -> {
                        exchangeRate.value = Resource.Loading()
                    }
                    is Resource.Error -> {
                        exchangeRate.value = Resource.Error(message = it.message)
                    }
                    is Resource.Success -> {
                        exchangeRate.value = Resource.Success(getExchangeRate(it.data))
                    }
                }
            }
        }
    }

    private fun getExchangeRate(data: HashMap<String, HashMap<String, Double>>?): ExchangeRate? {

        if (data == null)
            return null

        val fromCurrency = data.keys.toList()[0].substringBefore('_')
        val toCurrency = data.keys.toList()[0].substringAfter('_')
        val theSortedMap =
            data[fromCurrency.plus('_').plus(toCurrency)]?.toSortedMap(reverseOrder())

        // update today rate value
        val todayValue = theSortedMap?.values!!.toList()[0]
        currentRate.value = todayValue

        val list7DaysKeys = theSortedMap.keys.toList()
            .subList(1, theSortedMap.keys.toList().size)

        val pairs = mutableListOf<Pair<String, Double>>()

        for (day in list7DaysKeys) {
            pairs.add(Pair(day, theSortedMap[day]!!))
        }

        return ExchangeRate(
            currencyFrom = fromCurrency,
            currencyTo = toCurrency,
            todayRate = todayValue,
            last7DaysRates = pairs
        )
    }

}