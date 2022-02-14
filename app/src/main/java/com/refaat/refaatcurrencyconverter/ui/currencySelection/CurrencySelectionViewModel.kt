package com.refaat.refaatcurrencyconverter.ui.currencySelection

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refaat.refaatcurrencyconverter.common.Resource
import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem
import com.refaat.refaatcurrencyconverter.domain.useCases.UseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencySelectionViewModel @Inject constructor(private val useCases: UseCases) :
    ViewModel() {
    val currenciesLiveData: MutableLiveData<Resource<List<CurrencyItem>>> = MutableLiveData()

    init {
        viewModelScope.launch {
            useCases.getCurrenciesUseCase.invoke().collect {
                currenciesLiveData.value = it
            }
        }
    }
}
