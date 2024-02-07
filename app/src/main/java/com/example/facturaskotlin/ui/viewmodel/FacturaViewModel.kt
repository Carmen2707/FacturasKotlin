package com.example.facturaskotlin.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.facturaskotlin.database.Factura
import com.example.facturaskotlin.repository.FacturaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * FacturaViewModel es el ViewModel que interactúa con el repositorio de facturas y proporciona datos a la actividad.
 */
@HiltViewModel
class FacturaViewModel @Inject constructor(private val facturaRepository: FacturaRepository) :
    ViewModel() {
    /**
     * Devuelve un LiveData que contiene la lista de facturas almacenadas en Room. Este LiveData
     * puede ser observado por la actividad para recibir actualizaciones en tiempo real.
     */
    fun getAllRepositoryList(): LiveData<List<Factura>> {
        return facturaRepository.getAllFromRoom()
    }

    /**
     * Realiza una llamada a la API a través del repositorio para obtener nuevas facturas y almacenarlas en Room.
     */
    fun makeApiCall() {
        facturaRepository.makeApiCall()
    }

    fun changeService(newDatos: String) {
        facturaRepository.setDatos(newDatos)
    }

}