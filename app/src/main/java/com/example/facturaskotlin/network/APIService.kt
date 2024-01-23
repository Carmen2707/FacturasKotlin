package com.example.facturaskotlin.network

import com.example.facturaskotlin.network.model.FacturaStructureResponse
import retrofit2.Call
import retrofit2.http.GET

interface APIService {
    @GET("facturas")
    fun getFacturas(): Call<FacturaStructureResponse>
}
