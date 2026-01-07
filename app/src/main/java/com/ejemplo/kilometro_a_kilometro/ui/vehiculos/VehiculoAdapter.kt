package com.ejemplo.kilometro_a_kilometro.ui.vehiculos

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.domain.model.Vehiculo

class VehiculoAdapter(
    private val vehiculos: List<Vehiculo>,
    private val vehiculoActivoId: Int?,              // ⭐ NUEVO
    private val onClick: (Vehiculo) -> Unit
) : RecyclerView.Adapter<VehiculoAdapter.VehiculoViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    class VehiculoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvDetalle: TextView = view.findViewById(R.id.tvDetalle)
        val layout: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehiculoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehiculo, parent, false)
        return VehiculoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehiculoViewHolder, position: Int) {
        val vehiculo = vehiculos[position]

        // ⭐ Mostrar vehículo activo
        val esActivo = vehiculo.id == vehiculoActivoId
        holder.tvTitulo.text =
            if (esActivo)
                "⭐ ${vehiculo.marca} ${vehiculo.modelo}"
            else
                "${vehiculo.marca} ${vehiculo.modelo}"

        holder.tvDetalle.text =
            "Matrícula: ${vehiculo.matricula} | ${vehiculo.combustible}"

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
                onClick(vehiculo)
            }
        }
    }

    override fun getItemCount(): Int = vehiculos.size

    // 🔹 Exponer vehículo seleccionado (borrar / activar)
    fun getVehiculoSeleccionado(): Vehiculo? {
        return if (selectedPosition != RecyclerView.NO_POSITION) {
            vehiculos[selectedPosition]
        } else {
            null
        }
    }
}
