package com.refaat.refaatcurrencyconverter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem
import kotlinx.coroutines.flow.Flow


@Dao
interface CurrenciesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrenciesList(currencies: List<CurrencyItem>)

    @Query("SELECT * from CurrencyItem")
    suspend fun getCurrenciesList(): List<CurrencyItem>


    @Query("SELECT * from CurrencyItem WHERE id=:code")
    suspend fun getCountry(code: String): CurrencyItem
}