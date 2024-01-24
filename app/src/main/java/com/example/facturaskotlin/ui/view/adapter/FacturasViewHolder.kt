package com.example.facturaskotlin.ui.view.adapter


import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.facturaskotlin.R
import com.example.facturaskotlin.database.Factura
import com.example.facturaskotlin.databinding.ItemFacturaBinding

/**
 * Representa la vista de un elemento de factura en el RecyclerView
 */
class FacturasViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemFacturaBinding.bind(view)

    fun render(item: Factura, onClickListener: (Factura) -> Unit) {
        binding.tvFecha.text = item.fecha
        binding.tvEstado.text = item.estado
        binding.tvPrecio.text = item.importe.toString()
        itemView.setOnClickListener {
            onClickListener(item)
        }
        //configuración del color del texto según su estado
        val estadoColor = when (binding.tvEstado.text) {
            "Pendiente de pago" -> ContextCompat.getColor(itemView.context, R.color.red)
            "Pagada" -> ContextCompat.getColor(itemView.context, R.color.green_pagada)
            else -> {
                ContextCompat.getColor(itemView.context, R.color.black)
            }
        }
        binding.tvEstado.setTextColor(estadoColor)
    }


}