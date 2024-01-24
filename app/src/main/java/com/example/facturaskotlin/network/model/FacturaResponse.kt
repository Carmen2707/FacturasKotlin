package com.example.facturaskotlin.network.model

import com.google.gson.annotations.SerializedName

/**
 * Representa la estructura de una respuesta de factura desde la API.
 */
data class FacturaResponse(
    @SerializedName("fecha") var fecha: String,
    @SerializedName("descEstado") var estado: String,
    @SerializedName("importeOrdenacion") var importe: Double
)
