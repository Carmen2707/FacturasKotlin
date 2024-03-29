package com.example.facturaskotlin.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.facturaskotlin.database.Factura
import com.example.facturaskotlin.database.FacturaDAO
import com.example.facturaskotlin.network.APIRetrofitService
import com.example.facturaskotlin.network.APIRetromockService
import com.example.facturaskotlin.network.APIService
import com.example.facturaskotlin.network.model.FacturaStructureResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 * FacturaRepository se encarga de manejar los datos relacionados con las facturas. Utiliza INJECT para permitir la inyección
 * de dependencias.
 */
class FacturaRepository @Inject constructor(
    private var retrofitService: APIRetrofitService,
    private var retromockService: APIRetromockService,
    private val facturaDAO: FacturaDAO
) {
    //utilizamos esta variable 'service' para determinar qué servicio de API utilizar según el valor de la variable 'datos'.
    private lateinit var service: APIService
    private var datos = "ficticio"

    /**
     * Este método determina que servicio de API se utilizará basándose en el valor de la variable 'datos' y lo asigna a la variable 'service'.
     */
    private fun decideService() {
        if (datos == "ficticio") {
            service = retromockService
        } else { //datos="real"
            service = retrofitService
        }
    }

    /**
     * Este método permite cambiar el valor de 'datos'.
     */
    fun setDatos(newDatos: String) {
        datos = newDatos
        decideService()
    }

    init {
        decideService()
    }


    /**
     * Este método devuelve un objeto LiveData con una lista de objetos Factura.
     */
    fun getAllFromRoom(): LiveData<List<Factura>> {
        return facturaDAO.getAll()
    }

    /**
     * Este método inserta en la base de datos el objeto pasado como párametro.
     */
    fun insertInRoom(factura: Factura) {
        facturaDAO.insertAll(factura)
    }

    /**
     * Este método realiza una llamada a la API para obtener la información de las facturas.
     */
    fun makeApiCall() {
        val call: Call<FacturaStructureResponse> = service.getFacturas()

        call.enqueue(object : Callback<FacturaStructureResponse> {
            //Se ejecuta si la llamada a la API fue exitosa.
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
                }
            }

            //Se ejecuta si la llamada a la API falla.
            override fun onFailure(call: Call<FacturaStructureResponse>, t: Throwable) {
                Log.d(
                    "FALLO DE CONEXIÓN",
                    "Se ha producido un problema al intentar establecer la conexión."
                )
            }


        })

    }


}