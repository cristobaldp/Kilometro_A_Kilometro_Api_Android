package com.ejemplo.kilometro_a_kilometro.ui.register

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.data.repository.UsuarioRepository
import com.ejemplo.kilometro_a_kilometro.ui.login.LoginActivity
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private val usuarioRepository = UsuarioRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellidos = findViewById<EditText>(R.id.etApellidos)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etTelefono = findViewById<EditText>(R.id.etTelefono)
        val spCiudad = findViewById<Spinner>(R.id.spCiudad)
        val etFechaNacimiento = findViewById<EditText>(R.id.etFechaNacimiento)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)

        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnYaTengoCuenta = findViewById<Button>(R.id.btnYaTengoCuenta)

        // =========================
        // 🔹 SPINNER CIUDADES (XML)
        // =========================
        val adapterCiudades = ArrayAdapter.createFromResource(
            this,
            R.array.ciudades,
            android.R.layout.simple_spinner_item
        )
        adapterCiudades.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spCiudad.adapter = adapterCiudades

        // =========================
        // 📅 DATE PICKER
        // =========================
        etFechaNacimiento.setOnClickListener {
            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->

                    // Formato yyyy-MM-dd (el que espera FastAPI)
                    val fechaFormateada = String.format(
                        Locale.getDefault(),
                        "%04d-%02d-%02d",
                        selectedYear,
                        selectedMonth + 1,
                        selectedDay
                    )

                    etFechaNacimiento.setText(fechaFormateada)
                },
                year,
                month,
                day
            )

            datePicker.show()
        }

        // =========================
        // 📝 REGISTRO
        // =========================
        btnRegistrar.setOnClickListener {

            val nombre = etNombre.text.toString().trim()
            val apellidos = etApellidos.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val ciudad = spCiudad.selectedItem.toString()
            val fechaNacimiento = etFechaNacimiento.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (
                nombre.isEmpty() || apellidos.isEmpty() || username.isEmpty() ||
                email.isEmpty() || telefono.isEmpty() ||
                fechaNacimiento.isEmpty() || password.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Rellena todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (ciudad == "Selecciona una ciudad") {
                Toast.makeText(
                    this,
                    "Selecciona una ciudad válida",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(
                    this,
                    "Las contraseñas no coinciden",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val usuario = usuarioRepository.register(
                    nombre,
                    apellidos,
                    username,
                    email,
                    telefono,
                    ciudad,
                    fechaNacimiento,
                    password
                )

                if (usuario != null) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Usuario registrado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(
                            this@RegisterActivity,
                            LoginActivity::class.java
                        )
                    )
                    finish()
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Error al registrar usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        btnYaTengoCuenta.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )
            finish()
        }
    }
}
