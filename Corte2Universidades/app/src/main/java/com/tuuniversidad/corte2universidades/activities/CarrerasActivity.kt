// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/activities/CarrerasActivity.kt
package com.tuuniversidad.corte2universidades.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuuniversidad.corte2universidades.adapters.CarreraAdapter
import com.tuuniversidad.corte2universidades.api.RetrofitClient
import com.tuuniversidad.corte2universidades.databinding.ActivityCarrerasBinding
import com.tuuniversidad.corte2universidades.models.Carrera
import com.tuuniversidad.corte2universidades.models.Universidad
import kotlinx.coroutines.launch

class CarrerasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarrerasBinding
    private lateinit var adapter: CarreraAdapter
    private var universidades: List<Universidad> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarrerasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Carreras"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        loadData()

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, CarreraFormActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    private fun setupRecyclerView() {
        adapter = CarreraAdapter(
            mutableListOf(),
            onItemClick = { carrera ->
                val intent = Intent(this, CarreraFormActivity::class.java).apply {
                    putExtra("carrera_id", carrera.id)
                    putExtra("nombre", carrera.nombre)
                    putExtra("numCreditos", carrera.numCreditos)
                    putExtra("numAsignaturas", carrera.numAsignaturas)
                    putExtra("numSemestres", carrera.numSemestres)
                    putExtra("nivelFormacion", carrera.nivelFormacion)
                    putExtra("titulo", carrera.titulo)
                    putExtra("valorSemestre", carrera.valorSemestre)
                    putExtra("universidad_id", carrera.universidadId)
                    putExtra("esAcreditada", carrera.esAcreditada)
                    putExtra("areaDelConocimiento", carrera.areaDelConocimiento)
                }
                startActivityForResult(intent, REQUEST_CODE)
            },
            onDeleteClick = { carrera -> confirmDelete(carrera) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun loadData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // Cargar universidades primero para resolver nombres
                val univResponse = RetrofitClient.apiService.getUniversidades(
                    apiKey = RetrofitClient.getApiKey(),
                    auth = RetrofitClient.getAuth()
                )
                if (univResponse.isSuccessful) {
                    universidades = univResponse.body() ?: emptyList()
                }

                // Cargar carreras
                val carrerasResponse = RetrofitClient.apiService.getCarreras(
                    apiKey = RetrofitClient.getApiKey(),
                    auth = RetrofitClient.getAuth()
                )
                if (carrerasResponse.isSuccessful) {
                    val carreras = carrerasResponse.body() ?: emptyList()
                    // Asignar nombre de universidad
                    carreras.forEach { carrera ->
                        carrera.universidadNombre = universidades
                            .find { it.id == carrera.universidadId }?.nombre ?: ""
                    }
                    adapter.updateList(carreras)
                    binding.tvEmpty.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
                }
            } catch (e: Exception) {
                Toast.makeText(this@CarrerasActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun confirmDelete(carrera: Carrera) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar carrera")
            .setMessage("¿Deseas eliminar '${carrera.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteCarrera(carrera)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteCarrera(carrera: Carrera) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteCarrera(
                    apiKey = RetrofitClient.getApiKey(),
                    auth = RetrofitClient.getAuth(),
                    idFilter = RetrofitClient.eqFilter(carrera.id!!)
                )
                if (response.isSuccessful) {
                    Toast.makeText(this@CarrerasActivity, "Carrera eliminada", Toast.LENGTH_SHORT).show()
                    loadData()
                } else {
                    Toast.makeText(this@CarrerasActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CarrerasActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            loadData()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val REQUEST_CODE = 200
    }
}
