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
/**
 * Módulo de Dagger que proporciona las dependencias necesarias para la inyección de dependencias mediante Hilt.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    /**
     * Este método devuelve una instancia de APIRetrofitService creada a partir de Retrofit.
     */
    @Provides
    @Singleton
    fun getRetrofit(retrofit: Retrofit): APIRetrofitService {
        return retrofit.create(APIRetrofitService::class.java)

    }

    /**
     * Este método devuelve una instancia de APIRetromockService creada a partir de Retromock.
     */
    @Provides
    @Singleton
    fun getRetromock(retromock: Retromock): APIRetromockService {
        return retromock.create(APIRetromockService::class.java)

    }

    /**
     * Este método construye una instancia de Retrofit con la urlBase y el convertidor Gson.
     */
    @Provides
    @Singleton
    fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://viewnextandroid.wiremockapi.cloud/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    /**
     * Este método construye una instancia de Retromock con la instancia de Retrofit.
     */
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