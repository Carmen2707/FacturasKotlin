package com.example.facturaskotlin.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.facturaskotlin.database.Factura
import com.example.facturaskotlin.repository.FacturaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FacturaViewModel @Inject constructor(private val facturaRepository: FacturaRepository) :
    ViewModel() {
    fun getAllRepositoryList(): LiveData<List<Factura>> {
        return facturaRepository.getAllFromRoom()
    }

    fun makeApiCall() {
        facturaRepository.makeApiCall()
    }

}