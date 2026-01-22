package com.ejemplo.kilometro_a_kilometro.ui.comparar

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.data.repository.RepostajeRepository
import com.ejemplo.kilometro_a_kilometro.data.repository.VehiculoRepository
import com.ejemplo.kilometro_a_kilometro.domain.model.Repostaje
import com.ejemplo.kilometro_a_kilometro.domain.model.Vehiculo
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class CompararVehiculosActivity : AppCompatActivity() {

    private lateinit var spVehiculo1: Spinner
    private lateinit var spVehiculo2: Spinner
    private lateinit var btnComparar: Button
    private lateinit var tvResultado: TextView

    private val vehiculoRepository = VehiculoRepository()
    private val repostajeRepository = RepostajeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comparar_vehiculos)

        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            finish()
            return
        }

        spVehiculo1 = findViewById(R.id.spVehiculo1)
        spVehiculo2 = findViewById(R.id.spVehiculo2)
        btnComparar = findViewById(R.id.btnComparar)
        tvResultado = findViewById(R.id.tvResultado)

        lifecycleScope.launch {

            // =========================
            // VEHÍCULOS DEL USUARIO
            // =========================
            val vehiculos = vehiculoRepository.getVehiculos(userId)

            if (vehiculos.size < 2) {
                Toast.makeText(
                    this@CompararVehiculosActivity,
                    "Necesitas al menos 2 vehículos",
                    Toast.LENGTH_LONG
                ).show()
                finish()
                return@launch
            }

            val nombres = vehiculos.map {
                "${it.marca} ${it.modelo}"
            }

            val adapter = ArrayAdapter(
                this@CompararVehiculosActivity,
                android.R.layout.simple_spinner_item,
                nombres
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            spVehiculo1.adapter = adapter
            spVehiculo2.adapter = adapter

            // =========================
            // COMPARAR
            // =========================
            btnComparar.setOnClickListener {

                val v1 = vehiculos[spVehiculo1.selectedItemPosition]
                val v2 = vehiculos[spVehiculo2.selectedItemPosition]

                if (v1.id == v2.id) {
                    Toast.makeText(
                        this@CompararVehiculosActivity,
                        "Selecciona vehículos distintos",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                lifecycleScope.launch {

                    val r1 = repostajeRepository.getRepostajes(v1.id)
                    val r2 = repostajeRepository.getRepostajes(v2.id)

                    val consumo1 = calcularConsumoMedio(r1)
                    val consumo2 = calcularConsumoMedio(r2)

                    val gasto1 = r1.sumOf { it.precioTotal }
                    val gasto2 = r2.sumOf { it.precioTotal }

                    val km1 = calcularKmTotales(r1)
                    val km2 = calcularKmTotales(r2)

                    tvResultado.text = """
🚗 ${v1.marca} ${v1.modelo}
⛽ Consumo: ${format(consumo1)} L/100km
💰 Gasto: ${format(gasto1)} €
🛣 Km: $km1 km

🚙 ${v2.marca} ${v2.modelo}
⛽ Consumo: ${format(consumo2)} L/100km
💰 Gasto: ${format(gasto2)} €
🛣 Km: $km2 km
                    """.trimIndent()
                }
            }
        }
    }

    // =========================
    // CÁLCULOS
    // =========================

    private fun calcularConsumoMedio(repostajes: List<Repostaje>): Double {
        if (repostajes.size < 2) return 0.0

        val ordenados = repostajes.sortedBy { it.kilometros }

        var litrosTotales = 0.0
        var kmTotales = 0

        for (i in 1 until ordenados.size) {
            val anterior = ordenados[i - 1]
            val actual = ordenados[i]

            val kmRecorridos = actual.kilometros - anterior.kilometros
            if (kmRecorridos > 0) {
                litrosTotales += anterior.litros
                kmTotales += kmRecorridos
            }
        }

        return if (kmTotales > 0) (litrosTotales / kmTotales) * 100 else 0.0
    }

    private fun calcularKmTotales(repostajes: List<Repostaje>): Int {
        if (repostajes.size < 2) return 0
        val ordenados = repostajes.sortedBy { it.kilometros }
        return ordenados.last().kilometros - ordenados.first().kilometros
    }

    private fun format(valor: Double): String {
        return ((valor * 100.0).roundToInt() / 100.0).toString()
    }
}
