// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/activities/UniversidadFormActivity.kt
package com.tuuniversidad.corte2universidades.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tuuniversidad.corte2universidades.api.RetrofitClient
import com.tuuniversidad.corte2universidades.databinding.ActivityUniversidadFormBinding
import com.tuuniversidad.corte2universidades.models.Universidad
import kotlinx.coroutines.launch

class UniversidadFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUniversidadFormBinding
    private var universidadId: Int? = null
    private val isEditing get() = universidadId != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUniversidadFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cargar datos si viene de edición
        universidadId = intent.getIntExtra("universidad_id", -1).takeIf { it != -1 }

        supportActionBar?.title = if (isEditing) "Editar Universidad" else "Nueva Universidad"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (isEditing) loadDataFromIntent()

        binding.btnSave.text = if (isEditing) "Actualizar" else "Guardar"
        binding.btnSave.setOnClickListener { saveUniversidad() }
    }

    private fun loadDataFromIntent() {
        binding.etNombre.setText(intent.getStringExtra("nombre") ?: "")
        binding.etCategoria.setText(intent.getStringExtra("categoria") ?: "")
        binding.etWeb.setText(intent.getStringExtra("web") ?: "")
        binding.etRector.setText(intent.getStringExtra("rector") ?: "")
        binding.etEmail.setText(intent.getStringExtra("email") ?: "")
        binding.etAcceso.setText(intent.getStringExtra("acceso") ?: "")
        binding.etTelefono.setText(intent.getStringExtra("telefono") ?: "")
        binding.etCiudad.setText(intent.getStringExtra("ciudad") ?: "")
        binding.etNumeroCarreras.setText(intent.getIntExtra("numeroCarreras", 0).toString())
        binding.etNumSedes.setText(intent.getIntExtra("numSedes", 0).toString())
        binding.etPais.setText(intent.getStringExtra("pais") ?: "")
        binding.etDepartamento.setText(intent.getStringExtra("departamento") ?: "")
    }

    private fun saveUniversidad() {
        if (!validateForm()) return

        val universidad = Universidad(
            nombre = binding.etNombre.text.toString().trim(),
            categoria = binding.etCategoria.text.toString().trim(),
            web = binding.etWeb.text.toString().trim(),
            rector = binding.etRector.text.toString().trim(),
            email = binding.etEmail.text.toString().trim(),
            acceso = binding.etAcceso.text.toString().trim(),
            telefono = binding.etTelefono.text.toString().trim(),
            ciudad = binding.etCiudad.text.toString().trim(),
            numeroCarreras = binding.etNumeroCarreras.text.toString().toIntOrNull() ?: 0,
            numSedes = binding.etNumSedes.text.toString().toIntOrNull() ?: 0,
            pais = binding.etPais.text.toString().trim(),
            departamento = binding.etDepartamento.text.toString().trim()
        )

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSave.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = if (isEditing) {
                    RetrofitClient.apiService.updateUniversidad(
                        apiKey = RetrofitClient.getApiKey(),
                        auth = RetrofitClient.getAuth(),
                        idFilter = RetrofitClient.eqFilter(universidadId!!),
                        universidad = universidad
                    )
                } else {
                    RetrofitClient.apiService.createUniversidad(
                        apiKey = RetrofitClient.getApiKey(),
                        auth = RetrofitClient.getAuth(),
                        universidad = universidad
                    )
                }

                if (response.isSuccessful) {
                    val msg = if (isEditing) "Universidad actualizada" else "Universidad creada"
                    Toast.makeText(this@UniversidadFormActivity, msg, Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this@UniversidadFormActivity,
                        "Error: ${response.code()} - ${response.errorBody()?.string()}",
                        Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@UniversidadFormActivity,
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.btnSave.isEnabled = true
            }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true
        if (binding.etNombre.text.isNullOrBlank()) {
            binding.etNombre.error = "Campo requerido"
            valid = false
        }
        if (binding.etCiudad.text.isNullOrBlank()) {
            binding.etCiudad.error = "Campo requerido"
            valid = false
        }
        if (binding.etPais.text.isNullOrBlank()) {
            binding.etPais.error = "Campo requerido"
            valid = false
        }
        return valid
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
