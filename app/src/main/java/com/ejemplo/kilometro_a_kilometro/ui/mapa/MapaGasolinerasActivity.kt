package com.ejemplo.kilometro_a_kilometro.ui.mapa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.data.repository.GasolineraRepository
import com.ejemplo.kilometro_a_kilometro.domain.model.Gasolinera
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapaGasolinerasActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val repository = GasolineraRepository()
    private val marcadores = HashMap<Marker, Gasolinera>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_gasolineras)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.btnBuscar).setOnClickListener {
            val localidad = findViewById<EditText>(R.id.etLocalidad).text.toString()
            if (localidad.isBlank()) {
                Toast.makeText(this, "Introduce una localidad", Toast.LENGTH_SHORT).show()
            } else {
                buscarGasolineras(localidad)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(40.4168, -3.7038), 6f
            )
        )

        map.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {

            override fun getInfoWindow(marker: Marker): View? = null

            override fun getInfoContents(marker: Marker): View {
                val view = LayoutInflater.from(this@MapaGasolinerasActivity)
                    .inflate(R.layout.info_window_gasolinera, null)

                val gasolinera = marcadores[marker]

                if (gasolinera != null) {

                    view.findViewById<TextView>(R.id.tvNombre).text =
                        gasolinera.nombre

                    view.findViewById<TextView>(R.id.tvDireccion).text =
                        "${gasolinera.direccion} (${gasolinera.municipio})"

                    val precios = StringBuilder()

                    fun add(nombre: String, valor: Double?) {
                        if (valor != null) {
                            precios.append("$nombre: %.3f €\n".format(valor))
                        }
                    }

                    add("Gasolina 95", gasolinera.gasolina95)
                    add("Gasolina 98", gasolinera.gasolina98)
                    add("Gasóleo A", gasolinera.gasoleoA)
                    add("Gasóleo Premium", gasolinera.gasoleoPremium)
                    add("Biodiesel", gasolinera.biodiesel)
                    add("Bioetanol", gasolinera.bioetanol)
                    add("GLP", gasolinera.glp)
                    add("GNC", gasolinera.gnc)
                    add("GNL", gasolinera.gnl)

                    view.findViewById<TextView>(R.id.tvPrecios).text =
                        if (precios.isNotEmpty())
                            precios.toString()
                        else
                            "Sin precios disponibles"
                }

                return view
            }
        })
    }

    private fun buscarGasolineras(localidad: String) {

        lifecycleScope.launch {

            val lista = withContext(Dispatchers.IO) {
                repository.buscarPorLocalidad(localidad)
            }

            map.clear()
            marcadores.clear()

            if (lista.isEmpty()) {
                Toast.makeText(
                    this@MapaGasolinerasActivity,
                    "No se encontraron gasolineras",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            lista.forEach { gasolinera ->

                val marker = map.addMarker(
                    MarkerOptions()
                        .position(LatLng(gasolinera.latitud, gasolinera.longitud))
                        .title(gasolinera.nombre)
                )

                if (marker != null) {
                    marcadores[marker] = gasolinera
                    marker.showInfoWindow()
                }
            }

            val primera = lista.first()
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(primera.latitud, primera.longitud),
                    12f
                )
            )
        }
    }
}
