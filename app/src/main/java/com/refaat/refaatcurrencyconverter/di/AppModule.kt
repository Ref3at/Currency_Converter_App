package com.refaat.refaatcurrencyconverter.di

import android.app.Application
import android.content.Context
import android.telephony.TelephonyManager
import androidx.room.Room
import com.refaat.refaatcurrencyconverter.data.localDataSource.AppDataBase
import com.refaat.refaatcurrencyconverter.data.remoteDataSource.CurrencyConverterAPI
import com.refaat.refaatcurrencyconverter.data.remoteDataSource.CustomOkHttpClient
import com.refaat.refaatcurrencyconverter.data.repository.CurrencyConverterRepositoryImpl
import com.refaat.refaatcurrencyconverter.domain.model.DeviceCode
import com.refaat.refaatcurrencyconverter.domain.useCases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDeviceCode(app: Application): DeviceCode {
        val telephoneManager =
            app.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return DeviceCode(telephoneManager.networkCountryIso.uppercase())
    }


    @Provides
    @Singleton
    fun providesAppDataBase(app: Application): AppDataBase {
        return Room.databaseBuilder(app, AppDataBase::class.java, AppDataBase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    @Singleton
    fun providesOkHttpClient(): CustomOkHttpClient {
        return CustomOkHttpClient
    }

    @Provides
    @Singleton
    fun providesCurrencyConverterAPI(customOkHttpClient: CustomOkHttpClient): CurrencyConverterAPI {
        return Retrofit.Builder()
            .baseUrl(CurrencyConverterAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(customOkHttpClient.getCustomOkHttpClient())
            .build().create(CurrencyConverterAPI::class.java)
    }


    @Provides
    @Singleton
    fun providesTheRepository(
        dataBase: AppDataBase,
        api: CurrencyConverterAPI
    ): CurrencyConverterRepositoryImpl {
        return CurrencyConverterRepositoryImpl(dataBase.dao, api)
    }


    @Provides
    @Singleton
    fun providesUseCases(
        repository: CurrencyConverterRepositoryImpl,
        deviceCode: DeviceCode
    ): UseCases {
        return UseCases(
            getCurrenciesUseCase = GetCurrenciesUseCase(repository),
            getExchangeRateUseCase = GetExchangeRateUseCase(repository),
            getDefaultCurrenciesUseCase = GetDefaultCurrenciesUseCase(repository, deviceCode)
        )
    }


}