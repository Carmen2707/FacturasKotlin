package com.example.facturaskotlin.ui.view

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.facturaskotlin.R
import com.example.facturaskotlin.database.Factura
import com.example.facturaskotlin.databinding.ActivityMainBinding
import com.example.facturaskotlin.ui.view.adapter.FacturasAdapter
import com.example.facturaskotlin.ui.viewmodel.FacturaViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapterFactura: FacturasAdapter
    private var filter: Filtro? = null
    private var maxImporteInicial: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapterFactura = FacturasAdapter() { factura -> onItemSelected(factura) }


        //cambiar el titulo de la toolbar
        setSupportActionBar(binding.included.toolbar)
        supportActionBar?.title = "Facturas"

        iniciarView()
        iniciarMainViewModel()


    }

    private fun iniciarMainViewModel() {
        val viewModel = ViewModelProvider(this).get(FacturaViewModel::class.java)
        viewModel.getAllRepositoryList().observe(this, Observer<List<Factura>> {
            val listaFacturas = obtenerListaGuardada()
            if (listaFacturas == null || listaFacturas.isEmpty()) {
                adapterFactura.setLista(it)
            } else {
                adapterFactura.setLista(listaFacturas)
            }
            adapterFactura.notifyDataSetChanged()


            if (it.isEmpty()) {
                viewModel.makeApiCall()

            }

            val filtro = intent.getStringExtra("filtro")
            if (filtro != null) {
                val filter = Gson().fromJson(filtro, Filtro::class.java)
                var listaFiltrada = it
                filter?.let { filter ->
                    listaFiltrada = filtrarPorFecha(filter.fechaDesde, filter.fechaHasta, listaFiltrada)
                    listaFiltrada = filtrarPorImporte(filter.importe, listaFiltrada)
                    listaFiltrada = filtrarPorEstado(filter.mapCheckBox, listaFiltrada)
                    Log.d("lista filtrada", listaFiltrada.toString())

                    guardarFiltro(listaFiltrada)
                    adapterFactura.setLista(listaFiltrada)
                    Log.d("FILTRO2", filter.toString())
                
                }

                if (listaFiltrada.isEmpty()) {
                    val mensaje = Dialog(this)
                    mensaje.setContentView(R.layout.activity_lista_vacia2)
                    mensaje.show()
                    //boton para cerrar el popup
                    val cerrarVentana = mensaje.findViewById<Button>(R.id.cerrarVentana)
                    cerrarVentana.setOnClickListener {
                        mensaje.dismiss()
                       val intent = Intent(this, FiltrosActivity::class.java)
                       /* val gson = Gson()
                        val filtroEnviar = Filtro(
                            filter.fechaDesde,
                            filter.fechaHasta,
                            filter.importe,
                            filter.mapCheckBox
                        )*/
                        intent.putExtra("maxImporte", filter.importe)
                        intent.putExtra("maxImporteInicial",maxImporteInicial)
                        Log.d("inicial",maxImporteInicial.toString())
                       // intent.putExtra("filtro", gson.toJson(filtroEnviar))
                        Log.d("hoala",filter.importe.toString())
                        startActivity(intent)
                    }
                }

            }


            maxImporteInicial = calcularMaximoImporte(it)

        })

    }

    private fun guardarFiltro(listaFiltrada: List<Factura>) {
        val gson = Gson()
        val filteredListJson = gson.toJson(listaFiltrada)

        val prefs: SharedPreferences = getPreferences(MODE_PRIVATE)
        prefs.edit().putString("FILTERED_LIST", filteredListJson).apply()
    }

    private fun obtenerListaGuardada(): List<Factura>? {
        val prefs: SharedPreferences = getPreferences(MODE_PRIVATE)
        val filteredListJson: String? = prefs.getString("FILTERED_LIST", null)

        return if (filteredListJson != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Factura>>() {}.type
            gson.fromJson(filteredListJson, type)
        } else {
            null


        }
    }

    private fun iniciarView() {
        binding.rvFacturas.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapterFactura = FacturasAdapter() { factura ->
                onItemSelected(factura)
            }
            adapter = adapterFactura
        }
    }

    private fun filtrarPorEstado(
        mapCheckBox: HashMap<String, Boolean>,
        listaFiltrada: List<Factura>
    ): List<Factura> {
        val listaEstado = mutableListOf<Factura>()
        val estadoPagadas = mapCheckBox.getValue("Pagada")
        val estadoAnuladas = mapCheckBox.getValue("Anulada")
        val estadoCuota = mapCheckBox.getValue("Cuota fija")
        val estadoPendientes = mapCheckBox.getValue("Pendiente de pago")
        val estadoPlan = mapCheckBox.getValue("Plan de pago")
        if (!estadoPagadas && !estadoAnuladas && !estadoCuota && !estadoPendientes && !estadoPlan) {
            return listaFiltrada
        }
        for (factura in listaFiltrada) {
            val checkeadoPagada = factura.estado == "Pagada"
            val checkeadoAnulada = factura.estado == "Anulada"
            val checkeadoCuota = factura.estado == "Cuota fija"
            val checkeadoPendientes = factura.estado == "Pendiente de pago"
            val checkeadoPlan = factura.estado == "Plan de pago"

            if ((checkeadoPagada && estadoPagadas) || (checkeadoAnulada && estadoAnuladas) || (checkeadoCuota && estadoCuota) || (checkeadoPendientes && estadoPendientes) || (checkeadoPlan && estadoPlan)) {
                listaEstado.add(factura)
            }
        }
        return listaEstado
    }


    private fun filtrarPorImporte(importe: Double, listaFiltrada: List<Factura>): List<Factura> {
        val listaImporte = mutableListOf<Factura>()
        for (factura in listaFiltrada) {
            if (factura.importe < importe) {
                listaImporte.add(factura)
            }
        }
        return listaImporte
    }

    private fun filtrarPorFecha(
        fechaDesde: String,
        fechaHasta: String,
        listaFiltrada: List<Factura>
    ): List<Factura> {
        val listaFecha = mutableListOf<Factura>()
        if (fechaDesde != getString(R.string.diaMesAño) && fechaHasta != getString(R.string.diaMesAño)) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            val fechaMinDate: Date? = sdf.parse(fechaDesde)
            val fechaMaxDate: Date? = sdf.parse(fechaHasta)

            for (factura in listaFiltrada) {
                val fecha = sdf.parse(factura.fecha)!!
                if (fecha.after(fechaMinDate) && fecha.before(fechaMaxDate)) {
                    listaFecha.add(factura)
                }
            }
        } else {
            return listaFiltrada
        }

        return listaFecha
    }

    private fun calcularMaximoImporte(listaFacturas: List<Factura>): Double {
        var maxImporte = 0.0

        for (factura in listaFacturas) {
            val maxFactura = factura.importe
            if (maxImporte < maxFactura) {
                maxImporte = maxFactura
            }
        }
        return maxImporte
    }

    private fun onItemSelected(factura: Factura) {
        //popup
        val dialogo = Dialog(this)
        dialogo.setContentView(R.layout.layout_popup)
        dialogo.show()
        //boton para cerrar el popup
        val cerrarButton = dialogo.findViewById<Button>(R.id.botonCerrarPopUp)
        cerrarButton.setOnClickListener {
            dialogo.dismiss()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filtro, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_filtro -> {
                val intent = Intent(this, FiltrosActivity::class.java)
                intent.putExtra("maxImporteInicial", maxImporteInicial)

                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


}
