// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/models/Usuario.kt
package com.tuuniversidad.corte2universidades.models

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("username") val username: String = "",
    @SerializedName("password") val password: String = "",
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("email") val email: String = ""
)
