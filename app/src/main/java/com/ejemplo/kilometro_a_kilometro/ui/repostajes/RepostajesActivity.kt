package com.ejemplo.kilometro_a_kilometro.ui.repostajes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.data.repository.RepostajeRepository
import com.ejemplo.kilometro_a_kilometro.data.repository.UsuarioRepository
import com.ejemplo.kilometro_a_kilometro.domain.model.Repostaje
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

class RepostajesActivity : AppCompatActivity() {

    private val repostajeRepository = RepostajeRepository()
    private val usuarioRepository = UsuarioRepository()

    private var repostajeSeleccionado: Repostaje? = null
    private lateinit var repostajes: List<Repostaje>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repostajes)

        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            finish()
            return
        }

        val tvConsumoMedio = findViewById<TextView>(R.id.tvConsumoMedio)
        val rvRepostajes = findViewById<RecyclerView>(R.id.rvRepostajes)
        val btnAddRepostaje = findViewById<Button>(R.id.btnAddRepostaje)
        val btnBorrarRepostaje = findViewById<Button>(R.id.btnBorrarRepostaje)
        val spAnio = findViewById<Spinner>(R.id.spFiltroAnio)
        val spMes = findViewById<Spinner>(R.id.spFiltroMes)

        rvRepostajes.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {

            val usuario = usuarioRepository.getUsuario(userId)
            val vehiculoId = usuario?.vehiculoActivoId ?: return@launch

            repostajes = repostajeRepository
                .getRepostajes(vehiculoId)
                .sortedBy { it.kilometros }

            // =========================
            // FILTROS
            // =========================
            val anios = repostajes
                .map { it.fecha.substring(0, 4).toInt() }
                .distinct()
                .sortedDescending()

            val meses = (1..12).toList()

            spAnio.adapter = ArrayAdapter(
                this@RepostajesActivity,
                android.R.layout.simple_spinner_item,
                anios
            ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

            spMes.adapter = ArrayAdapter(
                this@RepostajesActivity,
                android.R.layout.simple_spinner_item,
                meses.map {
                    LocalDate.of(2000, it, 1)
                        .month
                        .getDisplayName(java.time.format.TextStyle.FULL, Locale("es"))
                        .replaceFirstChar { c -> c.uppercase() }
                }
            ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

            fun aplicarFiltro() {
                val anio = spAnio.selectedItem as Int
                val mes = spMes.selectedItemPosition + 1

                val filtrados = repostajes.filter {
                    val partes = it.fecha.split("-")
                    partes[0].toInt() == anio && partes[1].toInt() == mes
                }

                rvRepostajes.adapter = RepostajeAdapter(filtrados) { r ->
                    repostajeSeleccionado = r
                    btnBorrarRepostaje.isEnabled = true
                }

                // 🔑 CONSUMO MEDIO DEL MES
                calcularConsumo(tvConsumoMedio, filtrados)
            }

            spAnio.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        aplicarFiltro()
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }

            spMes.onItemSelectedListener = spAnio.onItemSelectedListener

            aplicarFiltro()

            // =========================
            // AÑADIR
            // =========================
            btnAddRepostaje.setOnClickListener {
                startActivity(
                    Intent(this@RepostajesActivity, AddRepostajeActivity::class.java)
                        .putExtra("USER_ID", userId)
                        .putExtra("VEHICULO_ID", vehiculoId)
                )
            }

            // =========================
            // BORRAR
            // =========================
            btnBorrarRepostaje.setOnClickListener {
                val r = repostajeSeleccionado ?: return@setOnClickListener

                AlertDialog.Builder(this@RepostajesActivity)
                    .setTitle("Borrar repostaje")
                    .setMessage("¿Borrar repostaje del ${r.fecha}?")
                    .setPositiveButton("Sí") { _, _ ->
                        lifecycleScope.launch {
                            repostajeRepository.borrarRepostaje(r.id)
                            recreate()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }

    private fun calcularConsumo(tv: TextView, lista: List<Repostaje>) {
        if (lista.size < 2) {
            tv.text = "⛽ Consumo medio: -- L/100km"
            return
        }

        var litros = 0.0
        var kms = 0

        for (i in 1 until lista.size) {
            val ant = lista[i - 1]
            val act = lista[i]
            val km = act.kilometros - ant.kilometros
            if (km > 0) {
                litros += ant.litros
                kms += km
            }
        }

        tv.text =
            if (kms > 0)
                "⛽ Consumo medio: %.2f L/100km".format((litros / kms) * 100)
            else
                "⛽ Consumo medio: -- L/100km"
    }
}
