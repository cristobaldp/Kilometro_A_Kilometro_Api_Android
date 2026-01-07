package com.ejemplo.kilometro_a_kilometro.ui.ajustes

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

class AjustesActivity : AppCompatActivity() {

    private val usuarioRepository = UsuarioRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        // =========================
        // USER ID
        // =========================
        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            finish()
            return
        }

        // =========================
        // VIEWS
        // =========================
        val tvDatosUsuario = findViewById<TextView>(R.id.tvDatosUsuario)
        val etNuevaPassword = findViewById<EditText>(R.id.etNuevaPassword)
        val etConfirmarPassword = findViewById<EditText>(R.id.etConfirmarPassword)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarPassword)

        // =========================
        // 👤 CARGAR DATOS USUARIO
        // =========================
        lifecycleScope.launch {

            val usuario = usuarioRepository.getUsuario(userId)

            if (usuario == null) {
                Toast.makeText(
                    this@AjustesActivity,
                    "Error al cargar usuario",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@launch
            }

            tvDatosUsuario.text = """
                Nombre: ${usuario.nombre} ${usuario.apellidos}
                Usuario: ${usuario.username}
                Email: ${usuario.email}
                Teléfono: ${usuario.telefono}
                Ciudad: ${usuario.ciudad}
            """.trimIndent()
        }

        // =========================
        // 🔑 CAMBIAR CONTRASEÑA
        // =========================
        btnGuardar.setOnClickListener {

            val nueva = etNuevaPassword.text.toString().trim()
            val confirmar = etConfirmarPassword.text.toString().trim()

            // Validaciones
            if (nueva.isEmpty() || confirmar.isEmpty()) {
                toast("Rellena ambos campos")
                return@setOnClickListener
            }

            if (nueva.length < 6) {
                toast("La contraseña debe tener al menos 6 caracteres")
                return@setOnClickListener
            }

            if (nueva != confirmar) {
                toast("Las contraseñas no coinciden")
                return@setOnClickListener
            }

            lifecycleScope.launch {

                val ok = usuarioRepository.cambiarPassword(
                    userId = userId,
                    nuevaPassword = nueva
                )

                if (ok) {
                    toast("Contraseña actualizada correctamente")
                    etNuevaPassword.text.clear()
                    etConfirmarPassword.text.clear()
                } else {
                    toast("Error al actualizar contraseña")
                }
            }
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
