package com.refaat.refaatcurrencyconverter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem


@Dao
interface CurrenciesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrenciesList(currencies: List<CurrencyItem>)

    @Query("SELECT * FROM CurrencyItem ORDER BY currencyCode ASC")
    suspend fun getCurrenciesList(): List<CurrencyItem>

    @Query("SELECT * FROM CurrencyItem WHERE currencyCode = :code LIMIT 1")
    suspend fun getCurrency(code: String): CurrencyItem?
}
