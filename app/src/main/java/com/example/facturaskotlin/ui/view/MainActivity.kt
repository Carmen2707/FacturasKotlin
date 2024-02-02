package com.example.facturaskotlin.ui.view

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log

import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.facturaskotlin.R
import com.example.facturaskotlin.constantes.Constantes
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


/**
 * La clase MainActivity es la actividad principal que muestra la lista de facturas.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapterFactura: FacturasAdapter
    private var objFiltro: Filtro? = null
    private var maxImporte: Double = 0.0
    private lateinit var intentLaunch: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapterFactura = FacturasAdapter { onItemSelected() }

        //cambiar el titulo de la toolbar
        setSupportActionBar(binding.included.toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        //lineas divisoria para el recyclerview
        binding.rvFacturas.itemAnimator = DefaultItemAnimator()
        val decoration = DividerItemDecoration(this, RecyclerView.VERTICAL)
        binding.rvFacturas.addItemDecoration(decoration)
        iniciarView()
        iniciarMainViewModel()
        intentLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    val maxImporte = result.data?.extras?.getDouble(Constantes.MAX_IMPORTE) ?: 0.0
                    val filtroJson = result.data?.extras?.getString(Constantes.FILTRO_ENVIADO)

                }
            }

    }


    /**
     * Inicializa la vista del RecyclerView y configura el adaptador.
     */
    private fun iniciarView() {
        binding.rvFacturas.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)

            adapter = adapterFactura
        }
    }

    /**
     * Inicializar el ViewModel y observar cambios en la lista de facturas.
     * Aplicar filtros si se proporcionan al iniciar la actividad.
     */
    private fun iniciarMainViewModel() {
        val viewModel = ViewModelProvider(this).get(FacturaViewModel::class.java)
        //Cualquier cambio en la lista activará el Observer.
        viewModel.getAllRepositoryList().observe(this, Observer<List<Factura>> {

            // Obtiene la lista de facturas almacenada y actuliza el adaptador.
            val listaFacturas = obtenerListaGuardada()
            Log.d("listaguardada", listaFacturas.toString())
            if (listaFacturas.isNullOrEmpty()) {
                adapterFactura.setLista(it)
            } else {
                adapterFactura.setLista(listaFacturas)
            }
            adapterFactura.notifyDataSetChanged()
            Log.d("AQUI", listaFacturas.toString())
            // Si la lista del ViewModel está vacía, realiza una llamada a la API para obtener nuevas facturas.
            if (it.isEmpty()) {
                viewModel.makeApiCall()
            }

            val filtro = intent.getStringExtra(Constantes.FILTRO_ENVIADO)
            if (filtro != null) {
                objFiltro = Gson().fromJson(filtro, Filtro::class.java)
                var listaFiltrada = it

                // Aplica los filtros a la lista actual.
                objFiltro?.let { filtro1 ->
                    listaFiltrada =
                        filtrarPorFecha(filtro1.fechaDesde, filtro1.fechaHasta, listaFiltrada)
                    listaFiltrada = filtrarPorImporte(filtro1.importe, listaFiltrada)
                    listaFiltrada = filtrarPorEstado(filtro1.mapCheckBox, listaFiltrada)

                    //Guarda la lista filtrada en las shared preferences.
                    guardarFiltro(listaFiltrada)
                    adapterFactura.setLista(listaFiltrada)
                }

                // Si no hay ninguna factura con las características del filtro (la lista filtrada esta vacía), muestra un diálogo informando al usuario.
                if (listaFiltrada.isEmpty()) {
                    mostrarVentanaNoHayFacturas()
                }
            }
            maxImporte = calcularMaximoImporte(it)
        })
    }

    private fun mostrarVentanaNoHayFacturas() {
        val mensaje = Dialog(this)
        mensaje.setContentView(R.layout.activity_lista_vacia)
        mensaje.show()

        //boton para cerrar el popup
        val cerrarVentana = mensaje.findViewById<Button>(R.id.cerrarVentana)
        cerrarVentana.setOnClickListener {
            mensaje.dismiss()
            val intent = Intent(this, FiltrosActivity::class.java)
            intent.putExtra(Constantes.MAX_IMPORTE, maxImporte)
            intentLaunch.launch(intent)
        }
    }


    private fun guardarFiltro(listaFiltrada: List<Factura>) {
        val gson = Gson()
        val filteredListJson = gson.toJson(listaFiltrada)
        val preferences = getPreferences(MODE_PRIVATE)
        preferences.edit().putString(Constantes.LISTA_FILTRADA, filteredListJson).apply()
    }

    private fun obtenerListaGuardada(): List<Factura>? {
        val preferences = getPreferences(MODE_PRIVATE)
        val filteredListJson = preferences.getString(Constantes.LISTA_FILTRADA, null)

        return if (filteredListJson != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Factura>>() {}.type
            gson.fromJson(filteredListJson, type)
        } else {
            null
        }
    }

    private fun filtrarPorEstado(
        mapCheckBox: HashMap<String, Boolean>,
        listaFiltrada: List<Factura>
    ): List<Factura> {
        val listaEstado = mutableListOf<Factura>()
        val estadoPagadas = mapCheckBox.getValue(Constantes.PAGADAS)
        val estadoAnuladas = mapCheckBox.getValue(Constantes.ANULADAS)
        val estadoCuota = mapCheckBox.getValue(Constantes.CUOTA_FIJA)
        val estadoPendientes = mapCheckBox.getValue(Constantes.PENDIENTE_PAGO)
        val estadoPlan = mapCheckBox.getValue(Constantes.PLAN_PAGO)
        if (!estadoPagadas && !estadoAnuladas && !estadoCuota && !estadoPendientes && !estadoPlan) {
            return listaFiltrada
        }
        for (factura in listaFiltrada) {
            val checkeadoPagada = factura.estado == Constantes.PAGADAS
            val checkeadoAnulada = factura.estado == Constantes.ANULADAS
            val checkeadoCuota = factura.estado == Constantes.CUOTA_FIJA
            val checkeadoPendientes = factura.estado == Constantes.PENDIENTE_PAGO
            val checkeadoPlan = factura.estado == Constantes.PLAN_PAGO

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
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        if (fechaDesde != getString(R.string.FiltroBtnDiaMesAño) && fechaHasta != getString(R.string.FiltroBtnDiaMesAño)) {
            val fechaMinDate: Date? = sdf.parse(fechaDesde)
            val fechaMaxDate: Date? = sdf.parse(fechaHasta)

            for (factura in listaFiltrada) {
                val fecha = sdf.parse(factura.fecha)!!
                if (fecha.after(fechaMinDate) && fecha.before(fechaMaxDate)) {
                    listaFecha.add(factura)
                }
            }
        } else if (fechaDesde != getString(R.string.FiltroBtnDiaMesAño)) {
            // Solo fechaDesde está establecido
            val fechaMinDate: Date? = sdf.parse(fechaDesde)
            for (factura in listaFiltrada) {
                val fecha = sdf.parse(factura.fecha)!!
                if (fecha.after(fechaMinDate)) {
                    listaFecha.add(factura)
                }
            }

        } else if (fechaHasta != getString(R.string.FiltroBtnDiaMesAño)) {
            // Solo fechaHasta está establecido
            val fechaMaxDate: Date? = sdf.parse(fechaHasta)

            for (factura in listaFiltrada) {
                val fecha = sdf.parse(factura.fecha)!!
                if (fecha.before(fechaMaxDate)) {
                    listaFecha.add(factura)
                }
            }
        } else {
            //si no hay ninguna fecha establecida
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

    /**
     * Mostrar un diálogo emergente con información sobre la factura seleccionada.
     */

    private fun onItemSelected() {
        val dialogo = Dialog(this)
        dialogo.setContentView(R.layout.layout_popup)
        dialogo.show()
        //boton para cerrar el popup
        val cerrarButton = dialogo.findViewById<Button>(R.id.botonCerrarPopUp)
        cerrarButton.setOnClickListener {
            dialogo.dismiss()
        }
    }

    /**
     * Configuración del menú.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filtro, menu)
        return true
    }

    /**
     * Configuración del botón para ir a la pantalla de filtros.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_filtro -> {
                //Se envia el filtro previo y el importe máximo.
                val intent = Intent(this, FiltrosActivity::class.java)
                intent.putExtra(Constantes.MAX_IMPORTE, maxImporte)
                if (objFiltro != null) {
                    val gson = Gson()
                    intent.putExtra(Constantes.FILTRO_ENVIADO, gson.toJson(objFiltro))
                }

                intentLaunch.launch(intent)

                true
            }


            else -> super.onOptionsItemSelected(item)
        }

    }
   /* override fun onBackPressed() {
        super.onBackPressed()
        // Cierra todas las actividades relacionadas con esta
        finishAffinity()
    }*/
}

