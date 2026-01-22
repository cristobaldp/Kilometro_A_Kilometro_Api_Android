package com.ejemplo.kilometro_a_kilometro.ui.mapa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ejemplo.kilometro_a_kilometro.R
import com.ejemplo.kilometro_a_kilometro.domain.model.Gasolinera
import com.ejemplo.kilometro_a_kilometro.service.GasolineraService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapaGasolinerasActivity :
    AppCompatActivity(),
    OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var gasolineraService: GasolineraService

    private val marcadores = mutableMapOf<Marker, Gasolinera>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_gasolineras)

        gasolineraService = GasolineraService(this)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val etLocalidad = findViewById<EditText>(R.id.etLocalidad)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)

        btnBuscar.setOnClickListener {
            val localidad = etLocalidad.text.toString().trim()
            if (localidad.isEmpty()) {
                Toast.makeText(this, "Introduce una localidad", Toast.LENGTH_SHORT).show()
            } else {
                buscarGasolineras(localidad)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // 📍 Vista inicial España
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(40.4168, -3.7038), 6f
            )
        )

        // 🪟 Ventana personalizada
        map.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {

            override fun getInfoWindow(marker: Marker): View? = null

            override fun getInfoContents(marker: Marker): View {
                val view = LayoutInflater.from(this@MapaGasolinerasActivity)
                    .inflate(R.layout.info_window_gasolinera, null)

                val gasolinera = marcadores[marker]!!

                view.findViewById<TextView>(R.id.tvNombre).text = gasolinera.nombre
                view.findViewById<TextView>(R.id.tvDireccion).text =
                    "${gasolinera.direccion} (${gasolinera.municipio})"

                val precios = StringBuilder()

                fun add(nombre: String, valor: Double?) {
                    if (valor != null && valor > 0) {
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

                return view
            }
        })
    }

    private fun buscarGasolineras(localidad: String) {
        map.clear()
        marcadores.clear()

        gasolineraService.obtenerGasolinerasPorLocalidad(
            localidad,
            onSuccess = { lista ->

                if (lista.isEmpty()) {
                    Toast.makeText(
                        this,
                        "No se encontraron gasolineras",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@obtenerGasolinerasPorLocalidad
                }

                lista.forEach { g ->
                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(LatLng(g.latitud, g.longitud))
                            .title(g.nombre)
                    )
                    if (marker != null) {
                        marcadores[marker] = g
                    }
                }

                // 📍 Centrar mapa en la primera
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(lista[0].latitud, lista[0].longitud),
                        12f
                    )
                )
            },
            onError = {
                Toast.makeText(
                    this,
                    "Error al obtener gasolineras",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}
