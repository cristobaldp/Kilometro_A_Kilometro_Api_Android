package com.ejemplo.kilometro_a_kilometro.ui.repostajes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.domain.model.Repostaje
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

sealed class RepostajeItem {
    data class Mes(val nombre: String) : RepostajeItem()
    data class Dato(val repostaje: Repostaje) : RepostajeItem()
}

class RepostajeAdapter(
    repostajes: List<Repostaje>,
    private val onClick: (Repostaje) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: List<RepostajeItem>

    init {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val agrupados = repostajes.groupBy {
            val fecha = LocalDate.parse(it.fecha, formatter)
            "${fecha.year}-${fecha.monthValue}"
        }

        val lista = mutableListOf<RepostajeItem>()

        agrupados.toSortedMap(compareByDescending { it }).forEach { (clave, reps) ->
            val partes = clave.split("-")
            val year = partes[0]
            val month = partes[1].toInt()

            val nombreMes = LocalDate.of(year.toInt(), month, 1)
                .month
                .getDisplayName(java.time.format.TextStyle.FULL, Locale("es"))
                .uppercase() + " $year"

            lista.add(RepostajeItem.Mes(nombreMes))
            reps.forEach { lista.add(RepostajeItem.Dato(it)) }
        }

        items = lista
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is RepostajeItem.Mes -> 0
            is RepostajeItem.Dato -> 1
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == 0) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mes, parent, false)
            MesViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_repostaje, parent, false)
            RepostajeViewHolder(view)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is RepostajeItem.Mes ->
                (holder as MesViewHolder).bind(item.nombre)

            is RepostajeItem.Dato ->
                (holder as RepostajeViewHolder).bind(item.repostaje, onClick)
        }
    }

    override fun getItemCount(): Int = items.size

    // --------------------
    // VIEW HOLDERS
    // --------------------

    class MesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvMes: TextView = view.findViewById(R.id.tvMes)
        fun bind(mes: String) {
            tvMes.text = mes
        }
    }

    class RepostajeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        private val tvDatos: TextView = view.findViewById(R.id.tvDatos)

        fun bind(repostaje: Repostaje, onClick: (Repostaje) -> Unit) {
            tvFecha.text = repostaje.fecha
            tvDatos.text =
                "⛽ %.1f L   💰 %.2f €   🚗 %d km"
                    .format(
                        repostaje.litros,
                        repostaje.precioTotal,
                        repostaje.kilometros
                    )

            itemView.setOnClickListener { onClick(repostaje) }
        }
    }
}
