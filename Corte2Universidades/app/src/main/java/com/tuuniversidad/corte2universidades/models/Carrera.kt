// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/models/Carrera.kt
package com.tuuniversidad.corte2universidades.models

import com.google.gson.annotations.SerializedName

data class Carrera(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("numCreditos") val numCreditos: Int = 0,
    @SerializedName("numAsignaturas") val numAsignaturas: Int = 0,
    @SerializedName("numSemestres") val numSemestres: Int = 0,
    @SerializedName("nivelFormacion") val nivelFormacion: String = "",
    @SerializedName("titulo") val titulo: String = "",
    @SerializedName("valorSemestre") val valorSemestre: Double = 0.0,
    @SerializedName("universidad_id") val universidadId: Int? = null,
    @SerializedName("esAcreditada") val esAcreditada: Boolean = false,
    @SerializedName("areaDelConocimiento") val areaDelConocimiento: String = "",
    // Campo virtual para mostrar nombre de universidad en lista
    var universidadNombre: String = ""
)
