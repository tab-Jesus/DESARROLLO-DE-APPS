// Archivo: app/src/main/java/com/tuuniversidad/corte2universidades/api/ApiService.kt
package com.tuuniversidad.corte2universidades.api

import com.tuuniversidad.corte2universidades.models.Carrera
import com.tuuniversidad.corte2universidades.models.Universidad
import com.tuuniversidad.corte2universidades.models.Usuario
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==================== USUARIOS ====================

    @GET("usuarios")
    suspend fun getUsuarios(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("email") emailFilter: String? = null,
        @Query("password") passwordFilter: String? = null
    ): Response<List<Usuario>>

    @PATCH("usuarios")
    suspend fun updateUsuarioPassword(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Query("email") emailFilter: String,
        @Body body: Map<String, String>
    ): Response<Any>

    // ==================== UNIVERSIDADES ====================

    @GET("universidades")
    suspend fun getUniversidades(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("order") order: String = "id.asc"
    ): Response<List<Universidad>>

    @GET("universidades")
    suspend fun getUniversidadesByPais(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("pais") paisFilter: String,
        @Query("order") order: String = "nombre.asc"
    ): Response<List<Universidad>>

    @GET("universidades")
    suspend fun getUniversidadesByNumCarreras(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("numeroCarreras") numCarrerasFilter: String,
        @Query("order") order: String = "nombre.asc"
    ): Response<List<Universidad>>

    @POST("universidades")
    suspend fun createUniversidad(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Prefer") prefer: String = "return=representation",
        @Body universidad: Universidad
    ): Response<List<Universidad>>

    @PATCH("universidades")
    suspend fun updateUniversidad(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Prefer") prefer: String = "return=representation",
        @Query("id") idFilter: String,
        @Body universidad: Universidad
    ): Response<List<Universidad>>

    @DELETE("universidades")
    suspend fun deleteUniversidad(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("id") idFilter: String
    ): Response<Any>

    // ==================== CARRERAS ====================

    @GET("carreras")
    suspend fun getCarreras(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("order") order: String = "id.asc"
    ): Response<List<Carrera>>

    @GET("carreras")
    suspend fun getCarrerasByUniversidad(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("universidad_id") universidadId: String,
        @Query("order") order: String = "nombre.asc"
    ): Response<List<Carrera>>

    @GET("carreras")
    suspend fun getCarrerasByNivel(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("nivelFormacion") nivelFilter: String,
        @Query("order") order: String = "nombre.asc"
    ): Response<List<Carrera>>

    @POST("carreras")
    suspend fun createCarrera(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Prefer") prefer: String = "return=representation",
        @Body carrera: Carrera
    ): Response<List<Carrera>>

    @PATCH("carreras")
    suspend fun updateCarrera(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Prefer") prefer: String = "return=representation",
        @Query("id") idFilter: String,
        @Body carrera: Carrera
    ): Response<List<Carrera>>

    @DELETE("carreras")
    suspend fun deleteCarrera(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("id") idFilter: String
    ): Response<Any>
}
