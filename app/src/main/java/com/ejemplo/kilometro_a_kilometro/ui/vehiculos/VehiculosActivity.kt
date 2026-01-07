package com.ejemplo.kilometro_a_kilometro.ui.vehiculos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.data.repository.VehiculoRepository
import com.ejemplo.kilometro_a_kilometro.domain.model.Vehiculo
import kotlinx.coroutines.launch

class VehiculosActivity : AppCompatActivity() {

    private val vehiculoRepository = VehiculoRepository()

    private var vehiculoSeleccionado: Vehiculo? = null
    private var vehiculoActivoId: Int? = null   // ⭐ NUEVO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehiculos)

        // =========================
        // USER ID
        // =========================
        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            Toast.makeText(this, "Usuario no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // =========================
        // BOTONES
        // =========================
        val btnAddVehiculo = findViewById<Button>(R.id.btnAddVehiculo)
        val btnBorrarVehiculo = findViewById<Button>(R.id.btnBorrarVehiculo)
        val btnVehiculoActivo = findViewById<Button>(R.id.btnVehiculoActivo)

        btnBorrarVehiculo.isEnabled = false
        btnVehiculoActivo.isEnabled = false

        btnAddVehiculo.setOnClickListener {
            startActivity(
                Intent(this, AddVehiculoActivity::class.java)
                    .putExtra("USER_ID", userId)
            )
        }

        // =========================
        // RECYCLER VIEW
        // =========================
        val rvVehiculos = findViewById<RecyclerView>(R.id.rvVehiculos)
        rvVehiculos.layoutManager = LinearLayoutManager(this)

        // =========================
        // CARGAR VEHÍCULOS
        // =========================
        lifecycleScope.launch {

            // 🔹 Obtener usuario para saber vehículo activo
            val usuario = vehiculoRepository.getUsuario(userId)
            vehiculoActivoId = usuario?.vehiculoActivoId

            val vehiculos = vehiculoRepository.getVehiculos(userId)

            if (vehiculos.isEmpty()) {
                Toast.makeText(
                    this@VehiculosActivity,
                    "No tienes vehículos registrados",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val adapter = VehiculoAdapter(
                vehiculos,
                vehiculoActivoId
            ) { vehiculo ->
                vehiculoSeleccionado = vehiculo
                btnBorrarVehiculo.isEnabled = true
                btnVehiculoActivo.isEnabled = true
            }

            rvVehiculos.adapter = adapter
        }

        // =========================
        // BORRAR VEHÍCULO
        // =========================
        btnBorrarVehiculo.setOnClickListener {

            val vehiculo = vehiculoSeleccionado ?: return@setOnClickListener

            AlertDialog.Builder(this)
                .setTitle("Borrar vehículo")
                .setMessage(
                    "¿Seguro que quieres borrar ${vehiculo.marca} ${vehiculo.modelo}?"
                )
                .setPositiveButton("Sí") { _, _ ->

                    lifecycleScope.launch {
                        val ok = vehiculoRepository.borrarVehiculo(vehiculo.id)

                        if (ok) {
                            Toast.makeText(
                                this@VehiculosActivity,
                                "Vehículo borrado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            recreate()
                        } else {
                            Toast.makeText(
                                this@VehiculosActivity,
                                "Error al borrar vehículo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // =========================
        // ESTABLECER VEHÍCULO ACTIVO ⭐
        // =========================
        btnVehiculoActivo.setOnClickListener {

            val vehiculo = vehiculoSeleccionado ?: return@setOnClickListener

            lifecycleScope.launch {
                val ok = vehiculoRepository.establecerVehiculoActivo(
                    userId,
                    vehiculo.id
                )

                if (ok) {
                    Toast.makeText(
                        this@VehiculosActivity,
                        "Vehículo activo actualizado",
                        Toast.LENGTH_SHORT
                    ).show()
                    recreate()
                } else {
                    Toast.makeText(
                        this@VehiculosActivity,
                        "Error al establecer vehículo activo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
