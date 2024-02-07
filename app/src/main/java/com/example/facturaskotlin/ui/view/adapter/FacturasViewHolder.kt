package com.example.facturaskotlin.ui.view.adapter


import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.facturaskotlin.R
import com.example.facturaskotlin.constantes.Constantes
import com.example.facturaskotlin.database.Factura
import com.example.facturaskotlin.databinding.ItemFacturaBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Representa la vista de un elemento de factura en el RecyclerView
 */
class FacturasViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemFacturaBinding.bind(view)

    fun render(item: Factura, onClickListener: (Factura) -> Unit) {
        binding.tvFecha.text = item.fecha.let { formatearFecha(it) }
        binding.tvEstado.text = item.estado
        binding.tvPrecio.text = item.importe.toString()
        itemView.setOnClickListener {
            onClickListener(item)
        }
        //configuración del color del texto según su estado
        if (binding.tvEstado.text == Constantes.PENDIENTE_PAGO) {
            val estadoColor = ContextCompat.getColor(itemView.context, R.color.red)
            binding.tvEstado.setTextColor(estadoColor)
        } else {
            binding.tvEstado.text = ""
        }


    }

    private fun formatearFecha(fechaString: String): String {
        try {
            val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fecha = formatoEntrada.parse(fechaString)

            // Formateo la fecha a "dd MMM yyyy" en español
            val formatoSalida = SimpleDateFormat(
                "dd MMM yyyy", Locale(
                    "es",
                    "ES"
                )
            )
            return formatoSalida.format(fecha!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            return fechaString // Devuelve la fecha original en caso de error
        }
    }
}

