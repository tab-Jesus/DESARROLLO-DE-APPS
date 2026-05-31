// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/activities/ReportesActivity.kt
package com.tuuniversidad.corte2universidades.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuuniversidad.corte2universidades.adapters.CarreraAdapter
import com.tuuniversidad.corte2universidades.adapters.UniversidadAdapter
import com.tuuniversidad.corte2universidades.api.RetrofitClient
import com.tuuniversidad.corte2universidades.databinding.ActivityReportesBinding
import com.tuuniversidad.corte2universidades.models.Universidad
import kotlinx.coroutines.launch

class ReportesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportesBinding
    private lateinit var univAdapter: UniversidadAdapter
    private lateinit var carreraAdapter: CarreraAdapter
    private var universidades: List<Universidad> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Reportes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupAdapters()
        loadUniversidades()
        setupListeners()
        setupNivelSpinner()

        // Por defecto mostrar reporte A
        showReport("A")
    }

    private fun setupAdapters() {
        univAdapter = UniversidadAdapter(mutableListOf(), {}, {})
        carreraAdapter = CarreraAdapter(mutableListOf(), {}, {})
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupNivelSpinner() {
        val niveles = listOf("Pregrado", "Posgrado", "Técnico", "Tecnológico", "Especialización", "Maestría", "Doctorado")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, niveles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerNivel.adapter = adapter
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
                    val adapter = ArrayAdapter(this@ReportesActivity,
                        android.R.layout.simple_spinner_item, nombres)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerUniversidad.adapter = adapter
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReportesActivity, "Error al cargar universidades", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnReporteA.setOnClickListener { showReport("A") }
        binding.btnReporteB.setOnClickListener { showReport("B") }
        binding.btnReporteC.setOnClickListener { showReport("C") }
        binding.btnReporteD.setOnClickListener { showReport("D") }

        binding.btnFiltrarA.setOnClickListener { runReporteA() }
        binding.btnFiltrarB.setOnClickListener { runReporteB() }
        binding.btnFiltrarC.setOnClickListener { runReporteC() }
        binding.btnFiltrarD.setOnClickListener { runReporteD() }
    }

    private fun showReport(report: String) {
        binding.layoutReporteA.visibility = if (report == "A") View.VISIBLE else View.GONE
        binding.layoutReporteB.visibility = if (report == "B") View.VISIBLE else View.GONE
        binding.layoutReporteC.visibility = if (report == "C") View.VISIBLE else View.GONE
        binding.layoutReporteD.visibility = if (report == "D") View.VISIBLE else View.GONE
        binding.recyclerView.adapter = null
        binding.tvResultCount.text = ""
    }

    private fun runReporteA() {
        val pais = binding.etPais.text.toString().trim()
        if (pais.isEmpty()) {
            binding.etPais.error = "Ingresa un país"
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getUniversidadesByPais(
                    apiKey = RetrofitClient.getApiKey(),
                    auth = RetrofitClient.getAuth(),
                    paisFilter = "eq.$pais"
                )
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    univAdapter.updateList(lista)
                    binding.recyclerView.adapter = univAdapter
                    binding.tvResultCount.text = "Resultados: ${lista.size} universidades en $pais"
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReportesActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun runReporteB() {
        val minCarreras = binding.etMinCarreras.text.toString().trim()
        if (minCarreras.isEmpty()) {
            binding.etMinCarreras.error = "Ingresa un número"
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getUniversidadesByNumCarreras(
                    apiKey = RetrofitClient.getApiKey(),
                    auth = RetrofitClient.getAuth(),
                    numCarrerasFilter = "gte.$minCarreras"
                )
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    univAdapter.updateList(lista)
                    binding.recyclerView.adapter = univAdapter
                    binding.tvResultCount.text = "Resultados: ${lista.size} universidades con >= $minCarreras carreras"
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReportesActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun runReporteC() {
        val pos = binding.spinnerUniversidad.selectedItemPosition
        if (pos < 0 || pos >= universidades.size) {
            Toast.makeText(this, "Selecciona una universidad", Toast.LENGTH_SHORT).show()
            return
        }
        val univ = universidades[pos]
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getCarrerasByUniversidad(
                    apiKey = RetrofitClient.getApiKey(),
                    auth = RetrofitClient.getAuth(),
                    universidadId = "eq.${univ.id}"
                )
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    lista.forEach { it.universidadNombre = univ.nombre }
                    carreraAdapter.updateList(lista)
                    binding.recyclerView.adapter = carreraAdapter
                    binding.tvResultCount.text = "Resultados: ${lista.size} carreras en ${univ.nombre}"
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReportesActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun runReporteD() {
        val nivel = binding.spinnerNivel.selectedItem.toString()
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getCarrerasByNivel(
                    apiKey = RetrofitClient.getApiKey(),
                    auth = RetrofitClient.getAuth(),
                    nivelFilter = "eq.$nivel"
                )
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    lista.forEach { carrera ->
                        carrera.universidadNombre = universidades
                            .find { it.id == carrera.universidadId }?.nombre ?: ""
                    }
                    carreraAdapter.updateList(lista)
                    binding.recyclerView.adapter = carreraAdapter
                    binding.tvResultCount.text = "Resultados: ${lista.size} carreras de nivel $nivel"
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReportesActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
