package com.ejemplo.kilometro_a_kilometro.ui.repostajes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.domain.model.Repostaje

class RepostajeAdapter(
    private val repostajes: List<Repostaje>
) : RecyclerView.Adapter<RepostajeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvDatos: TextView = view.findViewById(R.id.tvDatos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_repostaje, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val r = repostajes[position]
        holder.tvFecha.text = r.fecha
        holder.tvDatos.text =
            "Litros: ${r.litros} | Precio: ${r.precioTotal}€ | Km: ${r.kilometros}"
    }

    override fun getItemCount(): Int = repostajes.size
}
