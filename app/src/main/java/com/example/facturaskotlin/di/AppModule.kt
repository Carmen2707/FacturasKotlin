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
    /**
     * Configura la URL y el convertidor Gson.
     */
    @Provides
    @Singleton
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://viewnextandroid.wiremockapi.cloud/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Utiliza la instancia de Retrofit para crear una implementación de APIService.
     */
    @Provides
    @Singleton
    fun getService(retrofit: Retrofit): APIService {
        return retrofit.create(APIService::class.java)
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