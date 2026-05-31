// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/api/RetrofitClient.kt
package com.tuuniversidad.corte2universidades.api

import com.tuuniversidad.corte2universidades.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.SUPABASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    // Headers helper
    fun getApiKey() = Constants.SUPABASE_ANON_KEY
    fun getAuth() = "Bearer ${Constants.SUPABASE_ANON_KEY}"

    // Filtro eq para Supabase: eq.valor
    fun eqFilter(value: Any) = "eq.$value"
    fun gteFilter(value: Any) = "gte.$value"
}
