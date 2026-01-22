package com.ejemplo.kilometro_a_kilometro.ui.menu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.ui.ajustes.AjustesActivity
import com.ejemplo.kilometro_a_kilometro.ui.comparar.CompararVehiculosActivity
import com.ejemplo.kilometro_a_kilometro.ui.estadisticas.EstadisticasActivity
import com.ejemplo.kilometro_a_kilometro.ui.login.LoginActivity
import com.ejemplo.kilometro_a_kilometro.ui.mapa.MapaGasolinerasActivity
import com.ejemplo.kilometro_a_kilometro.ui.repostajes.RepostajesActivity
import com.ejemplo.kilometro_a_kilometro.ui.vehiculos.VehiculosActivity
import com.ejemplo.kilometro_a_kilometro.utils.SessionManager

class MenuPrincipalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        // =========================
        // 🔐 COMPROBAR SESIÓN
        // =========================
        val sessionManager = SessionManager(this)
        val userId = intent.getIntExtra("USER_ID", -1)

        if (!sessionManager.isLoggedIn() || userId == -1) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // =========================
        // 📂 NAVEGACIÓN
        // =========================
        findViewById<CardView>(R.id.cardVehiculos).setOnClickListener {
            startActivity(
                Intent(this, VehiculosActivity::class.java)
                    .putExtra("USER_ID", userId)
            )
        }

        findViewById<CardView>(R.id.cardRepostajes).setOnClickListener {
            startActivity(
                Intent(this, RepostajesActivity::class.java)
                    .putExtra("USER_ID", userId)
            )
        }

        findViewById<CardView>(R.id.cardEstadisticas).setOnClickListener {
            startActivity(
                Intent(this, EstadisticasActivity::class.java)
                    .putExtra("USER_ID", userId)
            )
        }

        findViewById<CardView>(R.id.cardComparar).setOnClickListener {
            startActivity(
                Intent(this, CompararVehiculosActivity::class.java)
                    .putExtra("USER_ID", userId)
            )
        }

       findViewById<CardView>(R.id.cardMapa).setOnClickListener {
            startActivity(Intent(this, MapaGasolinerasActivity::class.java))
       }

        findViewById<CardView>(R.id.cardAjustes).setOnClickListener {
            startActivity(
                Intent(this, AjustesActivity::class.java)
                    .putExtra("USER_ID", userId)
            )
        }

        // =========================
        // 🚪 CERRAR SESIÓN (REAL)
        // =========================
        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {

            sessionManager.clearSession()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
