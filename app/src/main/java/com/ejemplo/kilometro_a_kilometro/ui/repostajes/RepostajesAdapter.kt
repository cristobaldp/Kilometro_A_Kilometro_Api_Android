package com.ejemplo.kilometro_a_kilometro.ui.repostajes

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.domain.model.Repostaje

class RepostajeAdapter(
    private val repostajes: List<Repostaje>,
    private val onClick: (Repostaje) -> Unit
) : RecyclerView.Adapter<RepostajeAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvDatos: TextView = view.findViewById(R.id.tvDatos)
        val layout: View = view
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

        // 🔹 Resaltar selección
        holder.layout.setBackgroundColor(
            if (position == selectedPosition)
                Color.parseColor("#FFDDDD")
            else
                Color.TRANSPARENT
        )

        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                selectedPosition = pos
                notifyDataSetChanged()
                onClick(r)
            }
        }
    }

    override fun getItemCount(): Int = repostajes.size

    // 🔹 Exponer repostaje seleccionado
    fun getRepostajeSeleccionado(): Repostaje? {
        return if (selectedPosition != RecyclerView.NO_POSITION) {
            repostajes[selectedPosition]
        } else {
            null
        }
    }
}
