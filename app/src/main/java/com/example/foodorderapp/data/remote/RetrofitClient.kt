package com.example.foodorderapp.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // URL dasar harus berakhiran dengan slash '/' untuk Retrofit.
    private const val BASE_URL = "https://script.google.com/macros/s/AKfycbyNWmqQRcKBpfFBNGiTAYW4GkB_NGPbb46rgWNHEYdxAQVMJJuCDfNvVB4wSs_xR8vf/"

    // Logger untuk melihat apa yang dikirim dan diterima (sangat membantu debugging di Logcat)
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val instance: FoodApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(FoodApiService::class.java)
    }
}
