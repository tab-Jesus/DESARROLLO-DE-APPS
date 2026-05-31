// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/utils/Constants.kt
// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/utils/Constants.kt
package com.tuuniversidad.corte2universidades.utils

object Constants {
    // ===== SUPABASE CONFIG =====
    // URL de tu proyecto (debe terminar en /rest/v1/)
    const val SUPABASE_URL = "https://bltnbkfyukbmulmjqtxt.supabase.co/rest/v1/"

    // TU CLAVE PÚBLICA - REEMPLAZA ESTA LÍNEA
    const val SUPABASE_ANON_KEY = "sb_publishable_bnq0hyF-ZmZoEfU66vp79w_I7oI8ZZt"

    // ===== EMAILJS CONFIG (opcional por ahora) =====
    const val EMAILJS_SERVICE_ID = "service_temporal"
    const val EMAILJS_TEMPLATE_ID = "template_temporal"
    const val EMAILJS_USER_ID = "user_temporal"

    // ===== SHARED PREFERENCES =====
    const val PREFS_NAME = "AppPrefs"
    const val PREF_USER_EMAIL = "user_email"
    const val PREF_USER_NAME = "user_name"
    const val PREF_IS_LOGGED = "is_logged"
}