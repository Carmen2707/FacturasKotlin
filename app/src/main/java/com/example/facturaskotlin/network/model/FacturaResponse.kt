package com.example.facturaskotlin.network.model

import com.google.gson.annotations.SerializedName

data class FacturaResponse(
    @SerializedName("fecha") var fecha: String,
    @SerializedName("descEstado") var estado: String,
    @SerializedName("importeOrdenacion") var importe: Double
)

