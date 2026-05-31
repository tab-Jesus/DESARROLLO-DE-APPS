// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/utils/EmailJSHelper.kt
package com.tuuniversidad.corte2universidades.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object EmailJSHelper {

    private val client = OkHttpClient()

    /**
     * Envía un correo usando EmailJS con la nueva contraseña.
     * @param toEmail Email del destinatario
     * @param toName Nombre del destinatario
     * @param newPassword Nueva contraseña generada
     * @return true si fue exitoso, false en caso contrario
     */
    suspend fun sendPasswordEmail(
        toEmail: String,
        toName: String,
        newPassword: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("service_id", Constants.EMAILJS_SERVICE_ID)
                put("template_id", Constants.EMAILJS_TEMPLATE_ID)
                put("user_id", Constants.EMAILJS_USER_ID)
                put("template_params", JSONObject().apply {
                    put("to_email", toEmail)
                    put("to_name", toName)
                    put("new_password", newPassword)
                    put("app_name", "Corte2 Universidades")
                })
            }

            val body = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://api.emailjs.com/api/v1.0/email/send")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Genera una contraseña aleatoria de 6 dígitos
     */
    fun generatePassword(): String {
        return (100000..999999).random().toString()
    }
}
