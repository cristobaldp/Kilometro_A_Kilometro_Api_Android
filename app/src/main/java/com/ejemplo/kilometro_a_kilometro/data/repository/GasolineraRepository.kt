package com.ejemplo.kilometro_a_kilometro.data.repository

import com.ejemplo.kilometro_a_kilometro.domain.model.Gasolinera
import org.json.JSONObject
import java.net.URL

class GasolineraRepository {

    fun buscarPorLocalidad(localidad: String): List<Gasolinera> {

        val url =
            "https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/"

        val json = URL(url).readText()
        val root = JSONObject(json)
        val lista = root.getJSONArray("ListaEESSPrecio")

        val resultado = mutableListOf<Gasolinera>()

        fun precio(o: JSONObject, campo: String): Double? {
            return o.optString(campo)
                .replace(",", ".")
                .toDoubleOrNull()
        }

        for (i in 0 until lista.length()) {
            val o = lista.getJSONObject(i)

            if (!o.getString("Municipio")
                    .contains(localidad, ignoreCase = true)
            ) continue

            resultado.add(
                Gasolinera(
                    nombre = o.getString("Rótulo"),
                    direccion = o.getString("Dirección"),
                    municipio = o.getString("Municipio"),
                    latitud = o.getString("Latitud")
                        .replace(",", ".")
                        .toDouble(),
                    longitud = o.getString("Longitud (WGS84)")
                        .replace(",", ".")
                        .toDouble(),

                    gasolina95 = precio(o, "Precio Gasolina 95 E5"),
                    gasolina98 = precio(o, "Precio Gasolina 98 E5"),
                    gasoleoA = precio(o, "Precio Gasóleo A"),
                    gasoleoPremium = precio(o, "Precio Gasóleo Premium"),
                    biodiesel = precio(o, "Precio Biodiesel"),
                    bioetanol = precio(o, "Precio Bioetanol"),
                    glp = precio(o, "Precio Gases licuados del petróleo"),
                    gnc = precio(o, "Precio Gas Natural Comprimido"),
                    gnl = precio(o, "Precio Gas Natural Licuado")
                )
            )
        }

        return resultado
    }
}
