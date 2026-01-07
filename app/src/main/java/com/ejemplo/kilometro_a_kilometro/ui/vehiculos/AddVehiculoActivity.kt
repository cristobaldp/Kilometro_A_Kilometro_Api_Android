package com.ejemplo.kilometro_a_kilometro.ui.vehiculos

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.data.remote.dto.VehiculoCreateDto
import com.ejemplo.kilometro_a_kilometro.data.repository.VehiculoRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class AddVehiculoActivity : AppCompatActivity() {

    private val vehiculoRepository = VehiculoRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehiculo)

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
        val spTipo = findViewById<Spinner>(R.id.spTipo)
        val spMarca = findViewById<Spinner>(R.id.spMarca)
        val spModelo = findViewById<Spinner>(R.id.spModelo)
        val spCombustible = findViewById<Spinner>(R.id.spCombustible)

        val etMatricula = findViewById<EditText>(R.id.etMatricula)
        val etAnio = findViewById<EditText>(R.id.etAnio)
        val etConsumo = findViewById<EditText>(R.id.etConsumo)

        val btnGuardar = findViewById<Button>(R.id.btnGuardarVehiculo)

        // =========================
        // COMBUSTIBLE
        // =========================
        spCombustible.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Gasolina", "Diésel", "Híbrido", "Eléctrico")
        )

        // =========================
        // LEER JSON DESDE ASSETS
        // =========================
        val jsonText = assets.open("vehiculos.json")
            .bufferedReader()
            .use { it.readText() }

        val jsonObject = JSONObject(jsonText)

        // =========================
        // TIPO
        // =========================
        val tipos = jsonObject.keys().asSequence().toList()
        spTipo.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            tipos
        )

        // =========================
        // TIPO → MARCAS
        // =========================
        spTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val tipoSeleccionado = tipos[position]
                val marcasObject = jsonObject.getJSONObject(tipoSeleccionado)
                val marcas = marcasObject.keys().asSequence().toList()

                spMarca.adapter = ArrayAdapter(
                    this@AddVehiculoActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    marcas
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // =========================
        // MARCA → MODELOS
        // =========================
        spMarca.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val tipo = spTipo.selectedItem.toString()
                val marca = spMarca.selectedItem.toString()

                val modelosArray = jsonObject
                    .getJSONObject(tipo)
                    .getJSONArray(marca)

                val modelos = mutableListOf<String>()
                for (i in 0 until modelosArray.length()) {
                    modelos.add(modelosArray.getString(i))
                }

                spModelo.adapter = ArrayAdapter(
                    this@AddVehiculoActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    modelos
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // =========================
        // GUARDAR VEHÍCULO
        // =========================
        btnGuardar.setOnClickListener {

            val tipo = spTipo.selectedItem.toString()
            val marca = spMarca.selectedItem.toString()
            val modelo = spModelo.selectedItem.toString()
            val combustible = spCombustible.selectedItem.toString()

            val matricula = etMatricula.text.toString().trim().uppercase()
            val anioText = etAnio.text.toString().trim()
            val consumoText = etConsumo.text.toString().trim()

            if (
                matricula.isEmpty() ||
                anioText.isEmpty() ||
                consumoText.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Rellena todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // ✅ VALIDACIÓN DE MATRÍCULA
            if (!matriculaValida(matricula)) {
                Toast.makeText(
                    this,
                    "Matrícula inválida (ejemplo: 1234ABC)",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val anio = anioText.toIntOrNull()
            val consumo = consumoText.toDoubleOrNull()

            if (anio == null || consumo == null) {
                Toast.makeText(
                    this,
                    "Año o consumo inválidos",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val dto = VehiculoCreateDto(
                user_id = userId,
                tipo = tipo,
                marca = marca,
                modelo = modelo,
                matricula = matricula,
                anio = anio,
                combustible = combustible,
                consumo = consumo
            )

            lifecycleScope.launch {
                val ok = vehiculoRepository.crearVehiculo(dto)

                if (ok) {
                    Toast.makeText(
                        this@AddVehiculoActivity,
                        "Vehículo creado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@AddVehiculoActivity,
                        "Error al crear vehículo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // =========================
    // VALIDACIÓN MATRÍCULA (ESPAÑA)
    // =========================
    private fun matriculaValida(matricula: String): Boolean {
        val regex = Regex("^[0-9]{4}[A-Z]{3}$")
        return regex.matches(matricula)
    }
}
