package com.ejemplo.kilometro_a_kilometro.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.data.repository.UsuarioRepository
import com.ejemplo.kilometro_a_kilometro.ui.menu.MenuPrincipalActivity
import com.ejemplo.kilometro_a_kilometro.ui.register.RegisterActivity
import com.ejemplo.kilometro_a_kilometro.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val usuarioRepository = UsuarioRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // =========================
        // 🔐 AUTO-LOGIN
        // =========================
        val sessionManager = SessionManager(this)
        if (sessionManager.isLoggedIn()) {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.putExtra("USER_ID", sessionManager.getUserId())
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Introduce usuario y contraseña",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val usuario = usuarioRepository.login(username, password)

                if (usuario != null) {

                    // =========================
                    // 💾 GUARDAR SESIÓN
                    // =========================
                    sessionManager.saveSession(usuario.id, usuario.username)

                    Toast.makeText(
                        this@LoginActivity,
                        "Bienvenido ${usuario.nombre}",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(
                        this@LoginActivity,
                        MenuPrincipalActivity::class.java
                    )
                    intent.putExtra("USER_ID", usuario.id)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Credenciales incorrectas",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        btnRegister.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }
    }
}
