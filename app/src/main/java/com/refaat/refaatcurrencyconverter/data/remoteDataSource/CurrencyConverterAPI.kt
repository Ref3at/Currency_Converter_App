package com.refaat.refaatcurrencyconverter.data.remoteDataSource

import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyConverterAPI {

    companion object {
        const val BASE_URL = "https://free.currconv.com"
    }

    @GET("api/v7/countries")
    suspend fun getCurrenciesList(): HashMap<String, HashMap<String, CurrencyItem>>

    @GET("api/v7/convert")
    suspend fun getExchangeRate(
        @Query("q") from_to: String,
        @Query("date") startDate: String,
        @Query("endDate") endDate: String,
        @Query("compact") compact: String
    ): HashMap<String, HashMap<String, Double>>

}