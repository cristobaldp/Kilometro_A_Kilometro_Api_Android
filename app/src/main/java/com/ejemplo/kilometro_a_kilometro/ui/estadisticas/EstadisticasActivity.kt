package com.ejemplo.kilometro_a_kilometro.ui.estadisticas

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.data.repository.RepostajeRepository
import com.ejemplo.kilometro_a_kilometro.data.repository.UsuarioRepository
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import kotlinx.coroutines.launch
import java.util.Calendar

class EstadisticasActivity : AppCompatActivity() {

    private val usuarioRepository = UsuarioRepository()
    private val repostajeRepository = RepostajeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            finish()
            return
        }

        val spAnio = findViewById<Spinner>(R.id.spAnio)
        val spMes = findViewById<Spinner>(R.id.spMes)

        val anioActual = Calendar.getInstance().get(Calendar.YEAR)
        val anios = (anioActual - 5..anioActual + 1).toList()
        val meses = listOf(
            "Enero","Febrero","Marzo","Abril","Mayo","Junio",
            "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
        )

        spAnio.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, anios)
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spMes.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, meses)
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val anio = spAnio.selectedItem as Int
                val mes = spMes.selectedItemPosition + 1
                cargarEstadisticas(userId, anio, mes)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spAnio.onItemSelectedListener = listener
        spMes.onItemSelectedListener = listener
    }

    private fun cargarEstadisticas(userId: Int, anio: Int, mes: Int) {

        val tvConsumo = findViewById<TextView>(R.id.tvConsumo)
        val tvKm = findViewById<TextView>(R.id.tvKm)
        val tvGasto = findViewById<TextView>(R.id.tvGasto)
        val tvMensaje = findViewById<TextView>(R.id.tvMensajeSinDatos)

        val chartConsumo = findViewById<LineChart>(R.id.chartConsumo)
        val chartGasto = findViewById<BarChart>(R.id.chartGasto)

        lifecycleScope.launch {

            val usuario = usuarioRepository.getUsuario(userId)
            val vehiculoId = usuario?.vehiculoActivoId

            if (vehiculoId == null) {
                Toast.makeText(
                    this@EstadisticasActivity,
                    "Selecciona un vehículo activo",
                    Toast.LENGTH_LONG
                ).show()
                finish()
                return@launch
            }

            val repostajes = repostajeRepository.getRepostajes(vehiculoId)
                .filter {
                    val partes = it.fecha.split("-")
                    partes[0].toInt() == anio && partes[1].toInt() == mes
                }
                .sortedBy { it.kilometros }

            if (repostajes.size < 2) {
                tvMensaje.visibility = View.VISIBLE
                chartConsumo.clear()
                chartGasto.clear()

                tvConsumo.text = "Consumo medio: --"
                tvKm.text = "Kilómetros recorridos: --"
                tvGasto.text = "Gasto total: --"
                return@launch
            }

            tvMensaje.visibility = View.GONE

            var litrosTotales = 0.0
            var kmTotales = 0
            var gastoTotal = 0.0

            for (i in 1 until repostajes.size) {
                val anterior = repostajes[i - 1]
                val actual = repostajes[i]

                val kmRecorridos = actual.kilometros - anterior.kilometros
                if (kmRecorridos > 0) {
                    litrosTotales += anterior.litros
                    kmTotales += kmRecorridos
                    gastoTotal += anterior.precioTotal
                }
            }

            val consumoMedio = (litrosTotales / kmTotales) * 100

            tvConsumo.text = "Consumo medio: %.2f L/100km".format(consumoMedio)
            tvKm.text = "Kilómetros recorridos: $kmTotales km"
            tvGasto.text = "Gasto total: %.2f €".format(gastoTotal)

            // 🔑 LIMITAR DATOS DE LAS GRÁFICAS
            val maxDatos = 10
            val repostajesGrafica =
                if (repostajes.size > maxDatos) repostajes.takeLast(maxDatos)
                else repostajes

            // GRÁFICA CONSUMO
            val consumoEntries = repostajesGrafica.mapIndexed { index, r ->
                Entry(index.toFloat(), r.litros.toFloat())
            }

            val consumoDataSet = LineDataSet(consumoEntries, "Últimos repostajes").apply {
                color = Color.CYAN
                setCircleColor(Color.CYAN)
                lineWidth = 2f
                setDrawValues(false)
            }

            chartConsumo.apply {
                data = LineData(consumoDataSet)
                description.isEnabled = false
                axisRight.isEnabled = false
                animateX(500)
                invalidate()
            }

            // GRÁFICA GASTO
            val gastoEntries = repostajesGrafica.mapIndexed { index, r ->
                BarEntry(index.toFloat(), r.precioTotal.toFloat())
            }

            val gastoDataSet = BarDataSet(gastoEntries, "Últimos repostajes (€)").apply {
                color = Color.parseColor("#FF9800")
                setDrawValues(false)
            }

            chartGasto.apply {
                data = BarData(gastoDataSet)
                description.isEnabled = false
                axisRight.isEnabled = false
                animateY(500)
                invalidate()
            }
        }
    }
}
