// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/activities/MenuActivity.kt
package com.tuuniversidad.corte2universidades.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.tuuniversidad.corte2universidades.databinding.ActivityMenuBinding
import com.tuuniversidad.corte2universidades.utils.Constants

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)

        val userName = prefs.getString(Constants.PREF_USER_NAME, "Usuario") ?: "Usuario"
        binding.tvWelcome.text = "Bienvenido, $userName"

        binding.btnUniversidades.setOnClickListener {
            startActivity(Intent(this, UniversidadesActivity::class.java))
        }

        binding.btnCarreras.setOnClickListener {
            startActivity(Intent(this, CarrerasActivity::class.java))
        }

        binding.btnReportes.setOnClickListener {
            startActivity(Intent(this, ReportesActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Sí") { _, _ ->
                    prefs.edit().clear().apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
}
