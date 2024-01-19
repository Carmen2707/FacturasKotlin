package com.example.facturaskotlin.ui.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.facturaskotlin.R
import com.example.facturaskotlin.databinding.ActivityFiltrosBinding
import com.google.gson.Gson
import java.util.Calendar

class FiltrosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFiltrosBinding
    private lateinit var seekBar: SeekBar
    private lateinit var central: TextView
    private lateinit var botonDesde: Button
    private lateinit var botonHasta: Button
    private lateinit var botonEliminar: Button
    private lateinit var checkPagadas: CheckBox
    private lateinit var checkAnuladas: CheckBox
    private lateinit var checkCuota: CheckBox
    private lateinit var checkPendientes: CheckBox
    private lateinit var checkPlan: CheckBox
    private var filtro: Filtro? = null
    private var maxImporte=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFiltrosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //cambiar el titulo de la toolbar
        setSupportActionBar(binding.included.toolbar)
        supportActionBar?.title = "Filtrar facturas"
        iniciarComponentes()
        iniciarBotonesFechas()
        iniciarSeekbar()
        iniciarBotonAplicar()
        iniciarBotonEliminarFiltros()


    }

    private fun guardarEstadoFiltro(filtro: Filtro) {
        val prefs = getPreferences(MODE_PRIVATE)
        val gson = Gson()
        val filterJson = gson.toJson(filtro)

        prefs.edit().putString("FILTER_STATE", filterJson).apply()
    }

    private fun aplicarFiltrosGuardados() {
        val prefs = getPreferences(MODE_PRIVATE)
        val filterJson = prefs.getString("FILTER_STATE", null)

        if (filterJson != null) {
            val gson = Gson()
            filtro = gson.fromJson(filterJson, Filtro::class.java)
            filtro?.let { nonNullFilter ->
                loadFilters(nonNullFilter)
            }
        }
    }

    private fun loadFilters(filtro: Filtro) {
        binding.fechaDesde.text = filtro.fechaDesde
        binding.fechaHasta.text = filtro.fechaHasta
        binding.seekBar.progress=filtro.importe.toInt()
        binding.checkPagadas.isChecked = filtro.mapCheckBox["Pagada"] ?: false
        binding.checkAnuladas.isChecked = filtro.mapCheckBox["Anulada"] ?: false
        binding.checkCuota.isChecked = filtro.mapCheckBox["Cuota fija"] ?: false
        binding.checkPendientes.isChecked = filtro.mapCheckBox["Pendiente de pago"] ?: false
        binding.checkPlan.isChecked = filtro.mapCheckBox["Plan de pago"] ?: false
    }

    private fun iniciarComponentes() {
        botonEliminar = binding.botonEliminar
        seekBar = binding.seekBar
        central = binding.central

        botonDesde = binding.fechaDesde
        botonHasta = binding.fechaHasta
        checkPagadas = binding.checkPagadas
        checkAnuladas = binding.checkAnuladas
        checkPendientes = binding.checkPendientes
        checkCuota = binding.checkCuota
        checkPlan = binding.checkPlan
        aplicarFiltrosGuardados()
        val filterJson = intent.getStringExtra("filtro")
        if (filterJson != null) {
            val gson = Gson()
            filtro = gson.fromJson(filterJson, Filtro::class.java)
            filtro?.let { nonNullFilter ->
                loadFilters(nonNullFilter)
            }
        }

        val gson = Gson()

        val botonAplicar = binding.botonAplicar
        botonAplicar.setOnClickListener {
            actualizarFiltros()
            val estado = hashMapOf(
                "Pagada" to checkPagadas.isChecked,
                "Anulada" to checkAnuladas.isChecked,
                "Cuota fija" to checkCuota.isChecked,
                "Pendiente de pago" to checkPendientes.isChecked,
                "Plan de pago" to checkPlan.isChecked
            )
            val valorDesde = botonDesde.text.toString()
            val valorHasta = botonHasta.text.toString()
            val centralText = binding.central.text.toString().replace("€", "")
              val central: Double = centralText.toDouble()
            val filtroEnviar = Filtro(valorDesde, valorHasta, central, estado)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("filtro", gson.toJson(filtroEnviar))
            startActivity(intent)
        }
    }

    private fun iniciarBotonEliminarFiltros() {
        //boton para eliminar los filtros

        botonEliminar.setOnClickListener {
            botonDesde.text = getString(R.string.diaMesAño)
            botonHasta.text = getString(R.string.diaMesAño)
            central.text = "1€"
            binding.maximo.text = String.format("%d€", seekBar.max)
            seekBar.progress = 1
            checkPagadas.isChecked = false
            checkAnuladas.isChecked = false
            checkCuota.isChecked = false
            checkPendientes.isChecked = false
            checkPlan.isChecked = false
        }
    }

    private fun iniciarBotonAplicar() {
        //boton aplicar

    }

    private fun actualizarFiltros() {

        val estado = hashMapOf(
            "Pagada" to checkPagadas.isChecked,
            "Anulada" to checkAnuladas.isChecked,
            "Cuota fija" to checkCuota.isChecked,
            "Pendiente de pago" to checkPendientes.isChecked,
            "Plan de pago" to checkPlan.isChecked
        )
        val valorDesde = botonDesde.text.toString()
        val valorHasta = botonHasta.text.toString()
        val centralText = binding.central.text.toString().replace("€", "")
        val central: Double = centralText.toDouble()

        filtro = Filtro(valorDesde, valorHasta, central, estado)
        guardarEstadoFiltro(filtro!!)

    }

    private fun iniciarSeekbar() {
        //iniciar el slider para el importe
        val maxImporteInicial=intent.getDoubleExtra("maxImporteInicial",0.0).toInt()+1
         maxImporte = intent.getDoubleExtra("maxImporte", 0.0).toInt()
Log.d("ff",maxImporte.toString())

        seekBar.max = maxImporteInicial
        seekBar.progress = maxImporte
        binding.maximo.text = "${maxImporteInicial}€"
        central.text = "${maxImporte}€"

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                central.text = String.format("%d€", progress)
            }


            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //metodo vacio
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //metodo vacio
            }

        })
    }

    private fun iniciarBotonesFechas() {
        //iniciar el boton para la fecha desde

        binding.fechaDesde.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                this,
                { _, year1, monthOfYear, dayOfMonth ->
                    "$dayOfMonth/${monthOfYear + 1}/$year1".also { botonDesde.text = it }
                },
                year,
                month,
                day
            )
            dpd.show()

        }
        //iniciar el boton para la fecha hasta

        binding.fechaHasta.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                this,
                { _, year1, monthOfYear, dayOfMonth ->
                    "$dayOfMonth/${monthOfYear + 1}/$year1".also { botonHasta.text = it }
                },
                year,
                month,
                day
            )
            dpd.show()

        }


    }

    //configuración para el menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cerrar, menu)
        return true
    }

    //configuración para el botón de cerrar los filtros
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_cerrar -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}