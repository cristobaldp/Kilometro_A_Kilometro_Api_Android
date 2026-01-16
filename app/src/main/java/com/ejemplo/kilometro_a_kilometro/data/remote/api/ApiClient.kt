package com.ejemplo.kilometro_a_kilometro.data.remote.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL_EMULATOR = "http://10.0.2.2:8000/"
    private const val BASE_URL_DEVICE   = "http://192.168.1.132:8000/"

    // Cambiar aquí según dónde pruebes
    private const val BASE_URL = BASE_URL_DEVICE

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}