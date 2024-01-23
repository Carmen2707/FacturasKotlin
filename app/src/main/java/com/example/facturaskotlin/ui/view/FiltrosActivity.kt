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
import com.example.facturaskotlin.constantes.Constantes
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
        supportActionBar?.title = getString(R.string.tituloPaginaFiltros)
        iniciarComponentes()
        iniciarBotonesFechas()
        iniciarBotonAplicar()
        iniciarSeekbar()
        iniciarBotonEliminarFiltros()


    }

    private fun guardarEstadoFiltro(filtro: Filtro) {
        val prefs = getPreferences(MODE_PRIVATE)
        val gson = Gson()
        val filterJson = gson.toJson(filtro)

        prefs.edit().putString("FILTER_STATE", filterJson).putInt("SEEKBAR_PROGRESS", seekBar.progress).apply()
    }

    private fun aplicarFiltrosGuardados() {
        val prefs = getPreferences(MODE_PRIVATE)
        val filterJson = prefs.getString("FILTER_STATE", null)

        if (filterJson != null) {
            val gson = Gson()
            filtro = gson.fromJson(filterJson, Filtro::class.java)
            filtro?.let { nonNullFilter ->
                cargarFiltros(nonNullFilter)
            }

        }
    }

    private fun cargarFiltros(filtro: Filtro) {
        binding.fechaDesde.text = filtro.fechaDesde
        binding.fechaHasta.text = filtro.fechaHasta
        binding.seekBar.progress=filtro.importe.toInt()
        binding.checkPagadas.isChecked = filtro.mapCheckBox[Constantes.PAGADAS] ?: false
        binding.checkAnuladas.isChecked = filtro.mapCheckBox[Constantes.ANULADAS] ?: false
        binding.checkCuota.isChecked = filtro.mapCheckBox[Constantes.CUOTA_FIJA] ?: false
        binding.checkPendientes.isChecked = filtro.mapCheckBox[Constantes.PENDIENTE_PAGO] ?: false
        binding.checkPlan.isChecked = filtro.mapCheckBox[Constantes.PLAN_PAGO] ?: false
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
        val filtrar = intent.getStringExtra(Constantes.FILTRO_ENVIADO)
        if (filtrar != null) {
            val gson = Gson()
            filtro = gson.fromJson(filtrar, Filtro::class.java)
            filtro?.let { nonNullFilter ->
                cargarFiltros(nonNullFilter)
            }
        }

    }

    private fun iniciarBotonEliminarFiltros() {
        //boton para resetear los filtros
        botonEliminar.setOnClickListener {
            botonDesde.text = getString(R.string.diaMesAño)
            botonHasta.text = getString(R.string.diaMesAño)
            central.text = "1€"
            binding.maximo.text = getString(R.string.importe_formato,seekBar.max)
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
        val gson = Gson()
        val botonAplicar = binding.botonAplicar
        botonAplicar.setOnClickListener {
            actualizarFiltros()
            val estado = hashMapOf(
                Constantes.PAGADAS to checkPagadas.isChecked,
                Constantes.ANULADAS to checkAnuladas.isChecked,
                Constantes.CUOTA_FIJA to checkCuota.isChecked,
                Constantes.PENDIENTE_PAGO to checkPendientes.isChecked,
                Constantes.PLAN_PAGO to checkPlan.isChecked
            )
            val valorDesde = botonDesde.text.toString()
            val valorHasta = botonHasta.text.toString()
            val centralText = binding.central.text.toString().replace("€", "")
            val central: Double = centralText.toDouble()

            val filtroEnviar = Filtro(valorDesde, valorHasta, central, estado)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(Constantes.FILTRO_ENVIADO, gson.toJson(filtroEnviar))
            startActivity(intent)

        }

    }

    private fun actualizarFiltros() {

        val estado = hashMapOf(
            Constantes.PAGADAS to checkPagadas.isChecked,
            Constantes.ANULADAS to checkAnuladas.isChecked,
            Constantes.CUOTA_FIJA to checkCuota.isChecked,
            Constantes.PENDIENTE_PAGO to checkPendientes.isChecked,
            Constantes.PLAN_PAGO to checkPlan.isChecked
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
         maxImporte = intent.getDoubleExtra(Constantes.MAX_IMPORTE, 0.0).toInt()+1
Log.d("ff",maxImporte.toString())

        aplicarFiltrosGuardados()
        seekBar.max=maxImporte
        seekBar.progress = filtro?.importe?.toInt() ?: maxImporte
        binding.maximo.text = getString(R.string.importe_formato,maxImporte)
        central.text = getString(R.string.importe_formato, seekBar.progress)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                central.text = getString(R.string.importe_formato, progress)
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