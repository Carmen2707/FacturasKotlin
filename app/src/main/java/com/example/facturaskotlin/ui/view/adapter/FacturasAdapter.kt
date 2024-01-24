package com.example.facturaskotlin.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.facturaskotlin.R
import com.example.facturaskotlin.database.Factura

/**
 * FacturasAdapter se encarga de inflar las vistas de elementos de facturas, mantener y
 * gestionar la lista de facturas, y vincular los datos de las facturas con los ViewHolder.
 */
class FacturasAdapter(private val onClickListener: (Factura) -> Unit) :
    RecyclerView.Adapter<FacturasViewHolder>() {
    private var listaFacturas: List<Factura>? = null

    fun setLista(listaFacturas: List<Factura>) {
        this.listaFacturas = listaFacturas
    }

    /**
     * Inflar el diseño de la vista del elemento de factura y devolver el ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_factura, parent, false)
        return FacturasViewHolder(view)
    }

    /**
     * Devuelve la cantidad de elementos en la lista de facturas, o 0 si la lista es nula
     */
    override fun getItemCount(): Int {
        if (listaFacturas == null) return 0
        return listaFacturas?.size!!
    }

    /**
     * Vincula los datos del elemento de factura en la posición actual con el ViewHolder
     */
    override fun onBindViewHolder(holder: FacturasViewHolder, position: Int) {
        holder.render(listaFacturas?.get(position)!!, onClickListener)

    }

}
