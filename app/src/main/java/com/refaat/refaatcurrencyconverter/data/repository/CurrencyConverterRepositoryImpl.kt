package com.refaat.refaatcurrencyconverter.data.repository

import com.refaat.refaatcurrencyconverter.*
import com.refaat.refaatcurrencyconverter.common.COMPACT_TYPE
import com.refaat.refaatcurrencyconverter.common.GBR_CODE
import com.refaat.refaatcurrencyconverter.common.Resource
import com.refaat.refaatcurrencyconverter.common.USD_CODE
import com.refaat.refaatcurrencyconverter.data.CurrenciesDao
import com.refaat.refaatcurrencyconverter.data.remoteDataSource.CurrencyConverterAPI
import com.refaat.refaatcurrencyconverter.domain.repository.CurrencyConverterRepository
import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException


class CurrencyConverterRepositoryImpl(
    private val dao: CurrenciesDao,
    private val currencyConverterAPI: CurrencyConverterAPI
) : CurrencyConverterRepository {
    override fun getCurrencies(): Flow<Resource<List<CurrencyItem>>> = flow {

        emit(Resource.Loading()) // to start show the progress bar

        val currenciesList = dao.getCurrenciesList()

        if (currenciesList.isNotEmpty()) {
            emit(Resource.Success(currenciesList))
        } else {
            try {

                val remoteCurrenciesList = currencyConverterAPI.getCurrenciesList()
                val remoteCurrenciesList1 = remoteCurrenciesList.values
                val remoteCurrenciesList2 = remoteCurrenciesList1.map {
                    it.values
                }
                val listOfCurrencies = remoteCurrenciesList2.get(0).toList()
                dao.insertCurrenciesList(listOfCurrencies)

                val newCurrenciesList = dao.getCurrenciesList()
                emit(Resource.Success(newCurrenciesList))

            } catch (e: HttpException) {
                emit(
                    Resource.Error(
                        message = "Oops, something went wrong!",
                        data = currenciesList
                    )
                )

            } catch (e: IOException) {
                emit(
                    Resource.Error(
                        message = "Couldn't reach server, check your internet connection.",
                        data = currenciesList
                    )
                )
            }
        }
    }

    override fun getExchangeRate(
        from: String,
        to: String,
        startDate: String,
        endDate: String
    ): Flow<Resource<HashMap<String, HashMap<String, Double>>>> = flow {
        emit(Resource.Loading()) // to start show the progress bar
        try {
            val exchangeRate = currencyConverterAPI.getExchangeRate(
                from.plus('_').plus(to),
                startDate,
                endDate,
                COMPACT_TYPE
            )
            emit(Resource.Success(exchangeRate))
        } catch (e: HttpException) {
            emit(
                Resource.Error(
                    message = "Oops, something went wrong!",
                )
            )
        } catch (e: IOException) {
            emit(
                Resource.Error(
                    message = "Couldn't reach server, check your internet connection.",
                )
            )
        }

    }

    override suspend fun getDefaultCurrencies(code: String): Pair<CurrencyItem, CurrencyItem> {
        //The default two currencies are as follow
        // - Device currency
        // - USD or GBR if the Device currency is USD
        return if (code == USD_CODE) {
            Pair(dao.getCountry(USD_CODE), dao.getCountry(GBR_CODE))
        } else {
            Pair(dao.getCountry(code),dao.getCountry(USD_CODE))
        }
    }

}