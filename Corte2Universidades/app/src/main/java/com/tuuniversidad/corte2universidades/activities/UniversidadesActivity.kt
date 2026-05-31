// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/activities/UniversidadesActivity.kt
package com.tuuniversidad.corte2universidades.activities

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuuniversidad.corte2universidades.adapters.UniversidadAdapter
import com.tuuniversidad.corte2universidades.api.RetrofitClient
import com.tuuniversidad.corte2universidades.databinding.ActivityUniversidadesBinding
import com.tuuniversidad.corte2universidades.models.Universidad
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class UniversidadesActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityUniversidadesBinding
    private lateinit var adapter: UniversidadAdapter
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var proximitySensor: Sensor? = null

    // Control de agitación
    private var lastShakeTime = 0L
    private val SHAKE_THRESHOLD = 15f
    private val SHAKE_INTERVAL = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUniversidadesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Universidades"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        setupSensors()
        loadUniversidades()

        binding.fabAdd.setOnClickListener {
            startActivityForResult(
                Intent(this, UniversidadFormActivity::class.java),
                REQUEST_CODE
            )
        }
    }

    private fun setupRecyclerView() {
        adapter = UniversidadAdapter(
            mutableListOf(),
            onItemClick = { universidad ->
                val intent = Intent(this, UniversidadFormActivity::class.java).apply {
                    putExtra("universidad_id", universidad.id)
                    putExtra("nombre", universidad.nombre)
                    putExtra("categoria", universidad.categoria)
                    putExtra("web", universidad.web)
                    putExtra("rector", universidad.rector)
                    putExtra("email", universidad.email)
                    putExtra("acceso", universidad.acceso)
                    putExtra("telefono", universidad.telefono)
                    putExtra("ciudad", universidad.ciudad)
                    putExtra("numeroCarreras", universidad.numeroCarreras)
                    putExtra("numSedes", universidad.numSedes)
                    putExtra("pais", universidad.pais)
                    putExtra("departamento", universidad.departamento)
                }
                startActivityForResult(intent, REQUEST_CODE)
            },
            onDeleteClick = { universidad ->
                confirmDelete(universidad)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                val acceleration = magnitude - SensorManager.GRAVITY_EARTH

                if (acceleration > SHAKE_THRESHOLD) {
                    val now = System.currentTimeMillis()
                    if (now - lastShakeTime > SHAKE_INTERVAL) {
                        lastShakeTime = now
                        Toast.makeText(this, "¡Agitación detectada! Recargando lista...", Toast.LENGTH_SHORT).show()
                        loadUniversidades()
                    }
                }
            }
            Sensor.TYPE_PROXIMITY -> {
                val distance = event.values[0]
                if (distance < 5f) {
                    val count = adapter.getList().size
                    Toast.makeText(this, "Total de universidades registradas: $count", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun loadUniversidades() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getUniversidades(
                    apiKey = RetrofitClient.getApiKey(),
                    auth = RetrofitClient.getAuth()
                )
                if (response.isSuccessful) {
                    adapter.updateList(response.body() ?: emptyList())
                    binding.tvEmpty.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
                } else {
                    Toast.makeText(this@UniversidadesActivity,
                        "Error al cargar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@UniversidadesActivity,
                    "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun confirmDelete(universidad: Universidad) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar universidad")
            .setMessage("¿Deseas eliminar '${universidad.nombre}'? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteUniversidad(universidad)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteUniversidad(universidad: Universidad) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteUniversidad(
                    apiKey = RetrofitClient.getApiKey(),
                    auth = RetrofitClient.getAuth(),
                    idFilter = RetrofitClient.eqFilter(universidad.id!!)
                )
                if (response.isSuccessful) {
                    Toast.makeText(this@UniversidadesActivity,
                        "Universidad eliminada", Toast.LENGTH_SHORT).show()
                    loadUniversidades()
                } else {
                    Toast.makeText(this@UniversidadesActivity,
                        "Error al eliminar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@UniversidadesActivity,
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            loadUniversidades()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val REQUEST_CODE = 100
    }
}
