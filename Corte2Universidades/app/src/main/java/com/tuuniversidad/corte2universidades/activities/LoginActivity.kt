// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/activities/LoginActivity.kt
package com.tuuniversidad.corte2universidades.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tuuniversidad.corte2universidades.api.RetrofitClient
import com.tuuniversidad.corte2universidades.databinding.ActivityLoginBinding
import com.tuuniversidad.corte2universidades.utils.Constants
import com.tuuniversidad.corte2universidades.utils.EmailJSHelper
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)

        // Si ya hay sesión activa, ir al menú
        if (prefs.getBoolean(Constants.PREF_IS_LOGGED, false)) {
            goToMenu()
            return
        }

        binding.btnLogin.setOnClickListener { doLogin() }
        binding.tvForgotPassword.setOnClickListener { showForgotPasswordDialog() }
    }

    private fun doLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getUsuarios(
                    apiKey = RetrofitClient.getApiKey(),
                    auth = RetrofitClient.getAuth(),
                    emailFilter = "eq.$email",
                    passwordFilter = "eq.$password"
                )

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val user = response.body()!![0]
                    prefs.edit().apply {
                        putBoolean(Constants.PREF_IS_LOGGED, true)
                        putString(Constants.PREF_USER_EMAIL, user.email)
                        putString(Constants.PREF_USER_NAME, user.nombre)
                        apply()
                    }
                    Toast.makeText(this@LoginActivity, "Bienvenido, ${user.nombre}!", Toast.LENGTH_SHORT).show()
                    goToMenu()
                } else {
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = "Email o contraseña incorrectos"
                }
            } catch (e: Exception) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = "Error de conexión: ${e.message}"
            } finally {
                setLoading(false)
            }
        }
    }

    private fun showForgotPasswordDialog() {
        val dialogView = layoutInflater.inflate(
            android.R.layout.simple_list_item_1, null
        )
        val emailInput = android.widget.EditText(this).apply {
            hint = "Ingresa tu email registrado"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setPadding(40, 20, 40, 20)
        }

        AlertDialog.Builder(this)
            .setTitle("¿Olvidaste tu contraseña?")
            .setMessage("Ingresa tu email y recibirás una nueva contraseña.")
            .setView(emailInput)
            .setPositiveButton("Enviar") { _, _ ->
                val email = emailInput.text.toString().trim()
                if (email.isNotEmpty()) {
                    recoverPassword(email)
                } else {
                    Toast.makeText(this, "Ingresa un email válido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun recoverPassword(email: String) {
        setLoading(true)
        lifecycleScope.launch {
            try {
                // Verificar que el email existe
                val response = RetrofitClient.apiService.getUsuarios(
                    apiKey = RetrofitClient.getApiKey(),
                    auth = RetrofitClient.getAuth(),
                    emailFilter = "eq.$email"
                )

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val user = response.body()!![0]
                    val newPassword = EmailJSHelper.generatePassword()

                    // Actualizar contraseña en Supabase
                    val updateResponse = RetrofitClient.apiService.updateUsuarioPassword(
                        apiKey = RetrofitClient.getApiKey(),
                        auth = RetrofitClient.getAuth(),
                        emailFilter = "eq.$email",
                        body = mapOf("password" to newPassword)
                    )

                    if (updateResponse.isSuccessful) {
                        // Enviar email con EmailJS
                        val emailSent = EmailJSHelper.sendPasswordEmail(
                            toEmail = email,
                            toName = user.nombre,
                            newPassword = newPassword
                        )
                        if (emailSent) {
                            Toast.makeText(this@LoginActivity,
                                "Nueva contraseña enviada a $email", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@LoginActivity,
                                "Contraseña actualizada pero no se pudo enviar el email. Nueva clave: $newPassword",
                                Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity,
                            "Error al actualizar contraseña", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity,
                        "Email no encontrado en el sistema", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity,
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !loading
        binding.tvError.visibility = View.GONE
    }

    private fun goToMenu() {
        startActivity(Intent(this, MenuActivity::class.java))
        finish()
    }
}
