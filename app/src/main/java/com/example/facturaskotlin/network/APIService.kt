package com.example.facturaskotlin.network

import com.example.facturaskotlin.network.model.FacturaStructureResponse
import retrofit2.Call
import retrofit2.http.GET

/**
 * Interfaz que se comunica con la API mediante Retrofit. Utiliza una solicitud GET.
 */
interface APIService {
    @GET("facturas")
    fun getFacturas(): Call<FacturaStructureResponse>
}
