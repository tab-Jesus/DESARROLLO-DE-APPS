// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/activities/CarreraFormActivity.kt
package com.tuuniversidad.corte2universidades.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tuuniversidad.corte2universidades.api.RetrofitClient
import com.tuuniversidad.corte2universidades.databinding.ActivityCarreraFormBinding
import com.tuuniversidad.corte2universidades.models.Carrera
import com.tuuniversidad.corte2universidades.models.Universidad
import kotlinx.coroutines.launch

class CarreraFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarreraFormBinding
    private var carreraId: Int? = null
    private var universidades: List<Universidad> = emptyList()
    private val isEditing get() = carreraId != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarreraFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carreraId = intent.getIntExtra("carrera_id", -1).takeIf { it != -1 }

        supportActionBar?.title = if (isEditing) "Editar Carrera" else "Nueva Carrera"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNivelSpinner()
        loadUniversidades()

        binding.btnSave.text = if (isEditing) "Actualizar" else "Guardar"
        binding.btnSave.setOnClickListener { saveCarrera() }
    }

    private fun setupNivelSpinner() {
        val niveles = listOf("Pregrado", "Posgrado", "Técnico", "Tecnológico", "Especialización", "Maestría", "Doctorado")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, niveles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerNivel.adapter = adapter

        if (isEditing) {
            val nivelActual = intent.getStringExtra("nivelFormacion") ?: ""
            val pos = niveles.indexOf(nivelActual)
            if (pos >= 0) binding.spinnerNivel.setSelection(pos)
        }
    }

    private fun loadUniversidades() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getUniversidades(
                    apiKey = RetrofitClient.getApiKey(),
                    auth = RetrofitClient.getAuth()
                )
                if (response.isSuccessful) {
                    universidades = response.body() ?: emptyList()
                    val nombres = universidades.map { it.nombre }
                    val adapter = ArrayAdapter(this@CarreraFormActivity,
                        android.R.layout.simple_spinner_item, nombres)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerUniversidad.adapter = adapter

                    if (isEditing) {
                        val univId = intent.getIntExtra("universidad_id", -1)
                        val pos = universidades.indexOfFirst { it.id == univId }
                        if (pos >= 0) binding.spinnerUniversidad.setSelection(pos)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@CarreraFormActivity, "Error al cargar universidades", Toast.LENGTH_SHORT).show()
            }
        }

        if (isEditing) loadDataFromIntent()
    }

    private fun loadDataFromIntent() {
        binding.etNombre.setText(intent.getStringExtra("nombre") ?: "")
        binding.etNumCreditos.setText(intent.getIntExtra("numCreditos", 0).toString())
        binding.etNumAsignaturas.setText(intent.getIntExtra("numAsignaturas", 0).toString())
        binding.etNumSemestres.setText(intent.getIntExtra("numSemestres", 0).toString())
        binding.etTitulo.setText(intent.getStringExtra("titulo") ?: "")
        binding.etValorSemestre.setText(intent.getDoubleExtra("valorSemestre", 0.0).toString())
        binding.etAreaConocimiento.setText(intent.getStringExtra("areaDelConocimiento") ?: "")
        binding.checkEsAcreditada.isChecked = intent.getBooleanExtra("esAcreditada", false)
    }

    private fun saveCarrera() {
        if (!validateForm()) return

        val selectedUnivPos = binding.spinnerUniversidad.selectedItemPosition
        val selectedUniv = if (selectedUnivPos >= 0 && selectedUnivPos < universidades.size)
            universidades[selectedUnivPos] else null

        val carrera = Carrera(
            nombre = binding.etNombre.text.toString().trim(),
            numCreditos = binding.etNumCreditos.text.toString().toIntOrNull() ?: 0,
            numAsignaturas = binding.etNumAsignaturas.text.toString().toIntOrNull() ?: 0,
            numSemestres = binding.etNumSemestres.text.toString().toIntOrNull() ?: 0,
            nivelFormacion = binding.spinnerNivel.selectedItem.toString(),
            titulo = binding.etTitulo.text.toString().trim(),
            valorSemestre = binding.etValorSemestre.text.toString().toDoubleOrNull() ?: 0.0,
            universidadId = selectedUniv?.id,
            esAcreditada = binding.checkEsAcreditada.isChecked,
            areaDelConocimiento = binding.etAreaConocimiento.text.toString().trim()
        )

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSave.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = if (isEditing) {
                    RetrofitClient.apiService.updateCarrera(
                        apiKey = RetrofitClient.getApiKey(),
                        auth = RetrofitClient.getAuth(),
                        idFilter = RetrofitClient.eqFilter(carreraId!!),
                        carrera = carrera
                    )
                } else {
                    RetrofitClient.apiService.createCarrera(
                        apiKey = RetrofitClient.getApiKey(),
                        auth = RetrofitClient.getAuth(),
                        carrera = carrera
                    )
                }

                if (response.isSuccessful) {
                    val msg = if (isEditing) "Carrera actualizada" else "Carrera creada"
                    Toast.makeText(this@CarreraFormActivity, msg, Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this@CarreraFormActivity,
                        "Error: ${response.code()} - ${response.errorBody()?.string()}",
                        Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CarreraFormActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
        if (universidades.isEmpty()) {
            Toast.makeText(this, "Debes tener al menos una universidad registrada", Toast.LENGTH_SHORT).show()
            valid = false
        }
        return valid
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
