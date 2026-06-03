package com.example.myapplication

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Data Transfer Object untuk MenuItem dari API
data class MenuItemDto(
    val id: Int,
    val name: String,
    val price: Long,
    val category: String,
    val rating: Double,
    val isPopular: Boolean,
    val color: String, // Diambil sebagai String (Hex) dari Google Sheet
    val imageUrl: String
)

// Model untuk mengirim pesanan (POST)
data class OrderRequest(
    val customerEmail: String,
    val items: String,
    val totalPrice: Long
)

interface FoodApiService {
    @GET("exec")
    suspend fun getMenu(): List<MenuItemDto>

    @POST("exec")
    suspend fun placeOrder(@Body order: OrderRequest): Map<String, String>
}

object RetrofitClient {
    // URL dasar harus berakhiran dengan slash '/' untuk Retrofit.
    // Kita pindahkan bagian 'exec' ke interface (@GET dan @POST) agar lebih bersih.
    private const val BASE_URL = "https://script.google.com/macros/s/AKfycbwOKkZ8cKsGsCC-TXcs3z9yNL9YL2vEYnPedBqp57JC9sGPgin91IPQ6nIkbaozl51ApA/"

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
