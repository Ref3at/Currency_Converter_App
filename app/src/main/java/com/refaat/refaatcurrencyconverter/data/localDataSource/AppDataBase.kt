package com.refaat.refaatcurrencyconverter.data.localDataSource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.refaat.refaatcurrencyconverter.data.CurrenciesDao
import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem

@Database(
    entities = [CurrencyItem::class],
    version = 1
)
abstract class AppDataBase : RoomDatabase() {
    abstract val dao: CurrenciesDao
    companion object {
        const val DATABASE_NAME = "Currency_Converter_DB"
    }
}