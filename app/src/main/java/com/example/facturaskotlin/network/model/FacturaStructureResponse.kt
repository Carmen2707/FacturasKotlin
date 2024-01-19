package com.example.facturaskotlin.network.model

data class FacturaStructureResponse(val numFacturas: Int, val facturas: List<FacturaResponse>) {
}