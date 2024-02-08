package com.example.facturaskotlin.network

import co.infinum.retromock.meta.Mock
import co.infinum.retromock.meta.MockCircular
import co.infinum.retromock.meta.MockResponse
import co.infinum.retromock.meta.MockResponses
import com.example.facturaskotlin.network.model.FacturaStructureResponse
import retrofit2.Call
import retrofit2.http.GET
/**
 * Interfaz base(padre) para servicios de API.
 */
interface APIService {
    fun getFacturas(): Call<FacturaStructureResponse>
}


/**
 * Interfaz que se comunica con la API mediante Retrofit. Utiliza una solicitud GET.
 */

interface APIRetrofitService : APIService {
    @GET("facturas")
    override fun getFacturas(): Call<FacturaStructureResponse>
}
/**
 * Interfaz que define un servicio de API utilizando Retromock. Utiliza una solicitud GET.
 */
interface APIRetromockService : APIService {
    @Mock
    @MockResponses(
        MockResponse(body = "mock.json"),
        MockResponse(body = "mock2.json"),
        MockResponse(body = "mock3.json")
    )
    @MockCircular
    @GET("/")
    override fun getFacturas(): Call<FacturaStructureResponse>
}