// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/utils/Constants.kt
package com.tuuniversidad.corte2universidades.utils

object Constants {
    // ===== SUPABASE CONFIG =====
    // ⚠️ REEMPLAZA con los valores REALES de tu proyecto Supabase ⚠️
    const val SUPABASE_URL = "https://bltnbkfyukbmulmjqtxt.supabase.co/rest/v1/"

    // ===== EMAILJS CONFIG =====
    // Si no configuras EmailJS, la app igual funciona (solo muestra la clave en pantalla)
    const val EMAILJS_SERVICE_ID = "service_id_aqui"
    const val EMAILJS_TEMPLATE_ID = "template_id_aqui"
    const val EMAILJS_USER_ID = "user_id_aqui"

    // ===== SHARED PREFERENCES =====
    const val PREFS_NAME = "AppPrefs"
    const val PREF_USER_EMAIL = "user_email"
    const val PREF_USER_NAME = "user_name"
    const val PREF_IS_LOGGED = "is_logged"
}