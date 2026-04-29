package com.refaat.refaatcurrencyconverter.data.remoteDataSource

import com.refaat.refaatcurrencyconverter.domain.model.FrankfurterHistoryResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyConverterAPI {

    companion object {
        const val BASE_URL = "https://api.frankfurter.app"
    }

    @GET("currencies")
    suspend fun getCurrenciesList(): HashMap<String, String>

    @GET("{startDate}..{endDate}")
    suspend fun getExchangeRate(
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): FrankfurterHistoryResponse
}
