package com.example.facturaskotlin.di

import android.content.Context
import co.infinum.retromock.Retromock
import com.example.facturaskotlin.ResourceBodyFactory
import com.example.facturaskotlin.database.FacturaDAO
import com.example.facturaskotlin.database.FacturaDB
import com.example.facturaskotlin.network.APIRetrofitService
import com.example.facturaskotlin.network.APIRetromockService
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
    fun getRetrofit(retrofit: Retrofit): APIRetrofitService {
        return retrofit.create(APIRetrofitService::class.java)

    }

    @Provides
    @Singleton
    fun getRetromock(retromock: Retromock): APIRetromockService {
        return retromock.create(APIRetromockService::class.java)

    }

    @Provides
    @Singleton
    fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://viewnextandroid.wiremockapi.cloud/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    @Provides
    @Singleton
    fun buildRetromock(retrofit: Retrofit): Retromock {
        return Retromock.Builder()
            .retrofit(retrofit)
            .defaultBodyFactory(ResourceBodyFactory())
            .build()

    }

    /**
     * Utiliza la instancia de FacturaDB para obtener el DAO asociado.
     */
    @Provides
    @Singleton
    fun getAppDao(facturaDataBase: FacturaDB): FacturaDAO {
        return facturaDataBase.getAppDao()
    }

    /**
     * Utiliza el método estático getAppDBInstance de FacturaDB para obtener o crear una instancia única de la base de datos.
     */
    @Provides
    @Singleton
    fun getAppDatabase(@ApplicationContext context: Context): FacturaDB {
        return FacturaDB.getAppDBInstance(context)
    }
}