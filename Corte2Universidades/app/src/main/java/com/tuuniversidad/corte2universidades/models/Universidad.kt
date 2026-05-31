// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/models/Universidad.kt
package com.tuuniversidad.corte2universidades.models

import com.google.gson.annotations.SerializedName

data class Universidad(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("categoria") val categoria: String = "",
    @SerializedName("web") val web: String = "",
    @SerializedName("rector") val rector: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("acceso") val acceso: String = "",
    @SerializedName("telefono") val telefono: String = "",
    @SerializedName("ciudad") val ciudad: String = "",
    @SerializedName("numeroCarreras") val numeroCarreras: Int = 0,
    @SerializedName("numSedes") val numSedes: Int = 0,
    @SerializedName("pais") val pais: String = "",
    @SerializedName("departamento") val departamento: String = ""
)
