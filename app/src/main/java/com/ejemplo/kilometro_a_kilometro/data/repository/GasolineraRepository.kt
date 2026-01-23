package com.ejemplo.kilometro_a_kilometro.data.repository

import com.ejemplo.kilometro_a_kilometro.domain.model.Gasolinera
import org.json.JSONObject
import java.net.URL
import java.text.Normalizer

class GasolineraRepository {

    fun buscarPorLocalidad(localidad: String): List<Gasolinera> {

        val url =
            "https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/"

        val json = URL(url).readText()
        val root = JSONObject(json)
        val lista = root.getJSONArray("ListaEESSPrecio")

        val resultado = mutableListOf<Gasolinera>()

        // 🔹 Normalizar texto (quita tildes, mayúsculas, etc.)
        fun normalizar(texto: String): String {
            return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
                .uppercase()
                .trim()
        }

        // 🔹 Conversión segura de precios
        fun precio(o: JSONObject, campo: String): Double? {
            val valor = o.optString(campo).trim()
            if (valor.isEmpty()) return null
            return valor.replace(",", ".").toDoubleOrNull()
        }

        val localidadNormalizada = normalizar(localidad)

        for (i in 0 until lista.length()) {
            val o = lista.getJSONObject(i)

            val municipioApi = o.optString("Municipio")
            val municipioNormalizado = normalizar(municipioApi)

            // 🔍 Comparación robusta
            if (!municipioNormalizado.contains(localidadNormalizada)) continue

            resultado.add(
                Gasolinera(
                    nombre = o.optString("Rótulo"),
                    direccion = o.optString("Dirección"),
                    municipio = municipioApi,

                    latitud = o.optString("Latitud")
                        .replace(",", ".")
                        .toDoubleOrNull() ?: continue,

                    longitud = o.optString("Longitud (WGS84)")
                        .replace(",", ".")
                        .toDoubleOrNull() ?: continue,

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
