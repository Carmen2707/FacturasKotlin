package com.example.facturaskotlin.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.facturaskotlin.R
import com.example.facturaskotlin.database.Factura

class FacturasAdapter(private val onClickListener: (Factura) -> Unit) :
    RecyclerView.Adapter<FacturasViewHolder>() {
    private var listaFacturas: List<Factura>? = null

    fun setLista(listaFacturas: List<Factura>) {
        this.listaFacturas = listaFacturas
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_factura, parent, false)
        return FacturasViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (listaFacturas == null) return 0
        return listaFacturas?.size!!
    }

    override fun onBindViewHolder(holder: FacturasViewHolder, position: Int) {
        holder.render(listaFacturas?.get(position)!!, onClickListener)

    }

}