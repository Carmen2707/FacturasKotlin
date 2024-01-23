package com.example.facturaskotlin.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.facturaskotlin.database.Factura
import com.example.facturaskotlin.database.FacturaDAO
import com.example.facturaskotlin.network.APIService
import com.example.facturaskotlin.network.model.FacturaStructureResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class FacturaRepository @Inject constructor(
    private val service: APIService,
    private val facturaDAO: FacturaDAO
) {

    fun getAllFromRoom(): LiveData<List<Factura>> {
        return facturaDAO.getAll()
    }

    fun insertInRoom(factura: Factura) {
        facturaDAO.insertAll(factura)
    }

    fun makeApiCall() {
        val call: Call<FacturaStructureResponse> = service.getFacturas()

        call.enqueue(object : Callback<FacturaStructureResponse> {
            override fun onResponse(
                call: Call<FacturaStructureResponse>,
                response: Response<FacturaStructureResponse>
            ) {
                if (response.isSuccessful) {
                    facturaDAO.deleteAll()
                    response.body()?.facturas?.forEach {
                        insertInRoom(
                            Factura(
                                fecha = it.fecha,
                                estado = it.estado,
                                importe = it.importe
                            )
                        )
                    }
                } else {
                    showError()
                }
            }

            override fun onFailure(call: Call<FacturaStructureResponse>, t: Throwable) {
                showError()
            }


        })

    }

    fun showError() {
        Log.d("ERROR", "Ha ocurrido un error al establecer la conexión.")
    }
}