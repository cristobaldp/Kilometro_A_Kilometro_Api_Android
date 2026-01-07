package com.ejemplo.kilometro_a_kilometro.ui.repostajes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

class RepostajesActivity : AppCompatActivity() {

    private val repostajeRepository = RepostajeRepository()
    private val usuarioRepository = UsuarioRepository()

    private var repostajeSeleccionado: Repostaje? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repostajes)

        // =========================
        // USER ID
        // =========================
        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            Toast.makeText(this, "Usuario no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val tvConsumoMedio = findViewById<TextView>(R.id.tvConsumoMedio)
        val rvRepostajes = findViewById<RecyclerView>(R.id.rvRepostajes)
        val btnAddRepostaje = findViewById<Button>(R.id.btnAddRepostaje)
        val btnBorrarRepostaje = findViewById<Button>(R.id.btnBorrarRepostaje)

        btnBorrarRepostaje.isEnabled = false

        rvRepostajes.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {

            // =========================
            // 1️⃣ USUARIO + VEHÍCULO ACTIVO
            // =========================
            val usuario = usuarioRepository.getUsuario(userId)

            if (usuario == null || usuario.vehiculoActivoId == null) {
                Toast.makeText(
                    this@RepostajesActivity,
                    "Selecciona un vehículo activo primero",
                    Toast.LENGTH_LONG
                ).show()
                finish()
                return@launch
            }

            val vehiculoActivoId = usuario.vehiculoActivoId

            // =========================
            // 2️⃣ REPOSTAJES
            // =========================
            val repostajes =
                repostajeRepository.getRepostajes(vehiculoActivoId)
                    .sortedBy { it.kilometros }

            val adapter = RepostajeAdapter(repostajes) { repostaje ->
                repostajeSeleccionado = repostaje
                btnBorrarRepostaje.isEnabled = true
            }

            rvRepostajes.adapter = adapter

            // =========================
            // 3️⃣ CONSUMO MEDIO CORRECTO
            // =========================
            if (repostajes.size < 2) {
                tvConsumoMedio.text = "⛽ Consumo medio: -- L/100km"
            } else {

                var litrosTotales = 0.0
                var kmTotales = 0

                for (i in 1 until repostajes.size) {
                    val anterior = repostajes[i - 1]
                    val actual = repostajes[i]

                    val kmRecorridos =
                        actual.kilometros - anterior.kilometros

                    if (kmRecorridos > 0) {
                        litrosTotales += anterior.litros
                        kmTotales += kmRecorridos
                    }
                }

                if (kmTotales > 0) {
                    val consumoMedio =
                        (litrosTotales / kmTotales) * 100

                    tvConsumoMedio.text =
                        "⛽ Consumo medio: %.2f L/100km".format(consumoMedio)
                } else {
                    tvConsumoMedio.text = "⛽ Consumo medio: -- L/100km"
                }
            }

            // =========================
            // 4️⃣ AÑADIR REPOSTAJE
            // =========================
            btnAddRepostaje.setOnClickListener {
                startActivity(
                    Intent(
                        this@RepostajesActivity,
                        AddRepostajeActivity::class.java
                    )
                        .putExtra("USER_ID", userId)
                        .putExtra("VEHICULO_ID", vehiculoActivoId)
                )
            }

            // =========================
            // 5️⃣ BORRAR REPOSTAJE
            // =========================
            btnBorrarRepostaje.setOnClickListener {

                val repostaje = repostajeSeleccionado ?: return@setOnClickListener

                AlertDialog.Builder(this@RepostajesActivity)
                    .setTitle("Borrar repostaje")
                    .setMessage(
                        "¿Seguro que quieres borrar el repostaje del ${repostaje.fecha}?"
                    )
                    .setPositiveButton("Sí") { _, _ ->

                        lifecycleScope.launch {
                            val ok =
                                repostajeRepository.borrarRepostaje(repostaje.id)

                            if (ok) {
                                Toast.makeText(
                                    this@RepostajesActivity,
                                    "Repostaje borrado correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                recreate()
                            } else {
                                Toast.makeText(
                                    this@RepostajesActivity,
                                    "Error al borrar repostaje",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }
}
