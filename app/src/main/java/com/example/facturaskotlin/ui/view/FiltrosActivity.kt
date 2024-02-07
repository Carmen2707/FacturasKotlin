package com.example.facturaskotlin.ui.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.facturaskotlin.R
import com.example.facturaskotlin.constantes.Constantes
import com.example.facturaskotlin.databinding.ActivityFiltrosBinding
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * La clase FiltrosActivity representa la actividad encargada de gestionar y aplicar los filtros.
 */
class FiltrosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFiltrosBinding
    private lateinit var central: TextView
    private lateinit var botonDesde: Button
    private lateinit var botonHasta: Button

    private var filtro: Filtro? = null
    private var maxImporte = 0

    private lateinit var intentLaunch: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFiltrosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //cambiar el titulo de la toolbar
        setSupportActionBar(binding.included.toolbar)
        supportActionBar?.title = getString(R.string.FiltroTituloPaginaFiltros)

        iniciarComponentes()
        iniciarBotonesFechas()
        iniciarBotonAplicar()
        iniciarSeekbar()
        iniciarBotonEliminarFiltros()

        intentLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.extras?.getDouble(Constantes.MAX_IMPORTE) ?: 0.0
                    val filtroJson = result.data?.extras?.getString(Constantes.FILTRO_ENVIADO)
                    if (filtroJson != null) {
                        val gson = Gson()
                        gson.fromJson(filtroJson, MainActivity::class.java)

                    }
                }
            }
    }


    /**
     * Obtiene las shared preferences y convierte el objeto Filtro a formato JSON.
     * Guarda el estado del filtro y la posición actual de la SeekBar.
     */
    private fun guardarEstadoFiltro(filtro: Filtro) {
        val preferences = getPreferences(MODE_PRIVATE)
        val gson = Gson()
        val filterJson = gson.toJson(filtro)
        preferences.edit().putString(Constantes.ESTADO_FILTRO, filterJson).apply()
    }

    /**
     * Obtiene las shared preferences y recupera el estado del filtro guardado.
     * Si existe un filtro guardado, lo carga.
     */
    private fun aplicarFiltrosGuardados() {
        val preferences = getPreferences(MODE_PRIVATE)
        val filterJson = preferences.getString(Constantes.ESTADO_FILTRO, null)

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
        binding.seekBar.progress = filtro.importe.toInt()
        binding.checkPagadas.isChecked = filtro.mapCheckBox[Constantes.PAGADAS] ?: false
        binding.checkAnuladas.isChecked = filtro.mapCheckBox[Constantes.ANULADAS] ?: false
        binding.checkCuota.isChecked = filtro.mapCheckBox[Constantes.CUOTA_FIJA] ?: false
        binding.checkPendientes.isChecked = filtro.mapCheckBox[Constantes.PENDIENTE_PAGO] ?: false
        binding.checkPlan.isChecked = filtro.mapCheckBox[Constantes.PLAN_PAGO] ?: false
    }

    private fun iniciarComponentes() {
        central = binding.central
        botonDesde = binding.fechaDesde
        botonHasta = binding.fechaHasta


        //si hay filtros guardados, los aplica
        aplicarFiltrosGuardados()
        //cargamos el filtro enviado
        val filtrar = intent.getStringExtra(Constantes.FILTRO_ENVIADO)
        if (filtrar != null) {
            val gson = Gson()
            filtro = gson.fromJson(filtrar, Filtro::class.java)
            filtro?.let { nonNullFilter ->
                cargarFiltros(nonNullFilter)
            }
        }

    }

    //botón para resetear los filtros
    private fun iniciarBotonEliminarFiltros() {
        binding.botonEliminar.setOnClickListener {
            resetearFiltros()
        }
    }

    private fun resetearFiltros() {
        botonDesde.text = getString(R.string.FiltroBtnDiaMesAño)
        botonHasta.text = getString(R.string.FiltroBtnDiaMesAño)
        central.text = "1€"
        binding.maximo.text = getString(R.string.importe_formato, binding.seekBar.max)
        binding.seekBar.progress = 1
        binding.checkPagadas.isChecked = false
        binding.checkAnuladas.isChecked = false
        binding.checkCuota.isChecked = false
        binding.checkPendientes.isChecked = false
        binding.checkPlan.isChecked = false
    }

    private fun iniciarBotonAplicar() {
        val gson = Gson()
        binding.botonAplicar.setOnClickListener {
            actualizarFiltros()
            val estado = hashMapOf(
                Constantes.PAGADAS to binding.checkPagadas.isChecked,
                Constantes.ANULADAS to binding.checkAnuladas.isChecked,
                Constantes.CUOTA_FIJA to binding.checkCuota.isChecked,
                Constantes.PENDIENTE_PAGO to binding.checkPendientes.isChecked,
                Constantes.PLAN_PAGO to binding.checkPlan.isChecked
            )
            val valorDesde = botonDesde.text.toString()
            val valorHasta = botonHasta.text.toString()
            val centralText = binding.central.text.toString().replace("€", "")
            val central: Double = centralText.toDouble()

            val filtroEnviar = Filtro(valorDesde, valorHasta, central, estado)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(Constantes.FILTRO_ENVIADO, gson.toJson(filtroEnviar))

            intentLaunch.launch(intent)
            finish()
        }
    }

    /**
     * Actualiza el objeto Filtro con los valores actuales y lo guarda en las shared preferences.
     */
    private fun actualizarFiltros() {
        val estado = hashMapOf(
            Constantes.PAGADAS to binding.checkPagadas.isChecked,
            Constantes.ANULADAS to binding.checkAnuladas.isChecked,
            Constantes.CUOTA_FIJA to binding.checkCuota.isChecked,
            Constantes.PENDIENTE_PAGO to binding.checkPendientes.isChecked,
            Constantes.PLAN_PAGO to binding.checkPlan.isChecked
        )
        val valorDesde = botonDesde.text.toString()
        val valorHasta = botonHasta.text.toString()
        val centralText = binding.central.text.toString().replace("€", "")
        val central: Double = centralText.toDouble()

        filtro = Filtro(valorDesde, valorHasta, central, estado)
        guardarEstadoFiltro(filtro!!)
    }

    private fun iniciarSeekbar() {
        maxImporte = intent.getDoubleExtra(Constantes.MAX_IMPORTE, 0.0).toInt() + 1

        aplicarFiltrosGuardados()
        binding.seekBar.max = maxImporte
        binding.seekBar.progress = filtro?.importe?.toInt() ?: maxImporte
        binding.maximo.text = getString(R.string.importe_formato, maxImporte)
        central.text = getString(R.string.importe_formato, binding.seekBar.progress)

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                central.text = getString(R.string.importe_formato, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //método vacío
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //método vacío
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

            //controlar que la fecha final sea a partir de la fecha de inicio
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaBotonDesde = binding.fechaDesde.text.toString()
            val fechaBotonHasta = binding.fechaHasta.text.toString()
//TODO ARREGLAR LA RESTRICCION PARA LA FECHA HASTA
            /*  if (binding.fechaHasta.text != getText(R.string.FiltroBtnDiaMesAño)) {
                  val maxDate = simpleDateFormat.parse(fechaBotonHasta)!!
                  dpd.datePicker.maxDate=maxDate.time
              }*/

            if (binding.fechaDesde.text != getText(R.string.FiltroBtnDiaMesAño)) {

                val minDate = simpleDateFormat.parse(fechaBotonDesde)!!
                dpd.datePicker.minDate = minDate.time
            }

            dpd.show()
        }
    }

    /**
     * Configuración del menú.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cerrar, menu)
        return true
    }

    /**
     * Configuración para el botón de cerrar los filtros.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_cerrar -> {
                val intent = Intent(this, MainActivity::class.java)

                intentLaunch.launch(intent)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}