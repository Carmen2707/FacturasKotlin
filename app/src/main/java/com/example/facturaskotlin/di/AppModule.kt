package com.example.facturaskotlin.di

import android.content.Context
import com.example.facturaskotlin.database.FacturaDAO
import com.example.facturaskotlin.database.FacturaDB
import com.example.facturaskotlin.network.APIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://viewnextandroid.wiremockapi.cloud/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun getService(retrofit: Retrofit): APIService {
        return retrofit.create(APIService::class.java)
    }

    @Provides
    @Singleton
    fun getAppDao(facturaDataBase: FacturaDB): FacturaDAO {
        return facturaDataBase.getAppDao()
    }

    @Provides
    @Singleton
    fun getAppDatabase(@ApplicationContext context: Context): FacturaDB {
        return FacturaDB.getAppDBInstance(context)
    }
}