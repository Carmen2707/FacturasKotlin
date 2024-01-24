package com.example.facturaskotlin.network.model

/**
 * Representa la estructura de la respuesta completa que se obtiene al llamar a la API.
 */
data class FacturaStructureResponse(val numFacturas: Int, val facturas: List<FacturaResponse>)