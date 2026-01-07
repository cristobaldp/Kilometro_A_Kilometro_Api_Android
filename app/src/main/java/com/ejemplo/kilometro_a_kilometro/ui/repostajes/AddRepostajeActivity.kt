package com.ejemplo.kilometro_a_kilometro.ui.repostajes

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.data.repository.RepostajeRepository
import com.ejemplo.kilometro_a_kilometro.data.repository.UsuarioRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class AddRepostajeActivity : AppCompatActivity() {

    private val usuarioRepository = UsuarioRepository()
    private val repostajeRepository = RepostajeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_repostaje)

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
        // VIEWS
        // =========================
        val etFecha = findViewById<EditText>(R.id.etFecha)
        val etLitros = findViewById<EditText>(R.id.etLitros)
        val etPrecio = findViewById<EditText>(R.id.etPrecio)
        val etKilometros = findViewById<EditText>(R.id.etKilometros)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarRepostaje)

        // =========================
        // CALENDARIO
        // =========================
        val calendario = Calendar.getInstance()

        fun actualizarFecha() {
            val fecha = String.format(
                "%04d-%02d-%02d",
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH) + 1,
                calendario.get(Calendar.DAY_OF_MONTH)
            )
            etFecha.setText(fecha)
        }

        actualizarFecha()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendario.set(year, month, dayOfMonth)
                actualizarFecha()
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )

        etFecha.setOnClickListener { datePicker.show() }

        // =========================
        // GUARDAR REPOSTAJE
        // =========================
        btnGuardar.setOnClickListener {

            val fecha = etFecha.text.toString()
            val litros = etLitros.text.toString().toDoubleOrNull()
            val precio = etPrecio.text.toString().toDoubleOrNull()
            val kilometros = etKilometros.text.toString().toIntOrNull()

            if (litros == null || precio == null || kilometros == null) {
                Toast.makeText(
                    this,
                    "Rellena todos los campos correctamente",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {

                // 1️⃣ Obtener usuario
                val usuario = usuarioRepository.getUsuario(userId)

                if (usuario == null || usuario.vehiculoActivoId == null) {
                    Toast.makeText(
                        this@AddRepostajeActivity,
                        "Selecciona un vehículo activo primero",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                    return@launch
                }

                val vehiculoId = usuario.vehiculoActivoId

                // 2️⃣ Obtener repostajes previos
                val repostajesPrevios =
                    repostajeRepository.getRepostajes(vehiculoId)

                // 3️⃣ VALIDACIÓN DE KILÓMETROS
                if (repostajesPrevios.isNotEmpty()) {
                    val ultimoKm = repostajesPrevios.maxOf { it.kilometros }

                    if (kilometros <= ultimoKm) {
                        Toast.makeText(
                            this@AddRepostajeActivity,
                            "Los kilómetros deben ser mayores que $ultimoKm",
                            Toast.LENGTH_LONG
                        ).show()
                        return@launch
                    }
                }

                // 4️⃣ Crear repostaje
                val ok = repostajeRepository.crearRepostaje(
                    vehiculoId = vehiculoId,
                    fecha = fecha,
                    litros = litros,
                    precioTotal = precio,
                    kilometros = kilometros
                )

                if (ok) {
                    Toast.makeText(
                        this@AddRepostajeActivity,
                        "Repostaje guardado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@AddRepostajeActivity,
                        "Error al guardar el repostaje",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
