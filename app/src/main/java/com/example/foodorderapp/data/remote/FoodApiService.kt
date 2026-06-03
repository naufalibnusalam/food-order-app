package com.example.foodorderapp.data.remote

import com.example.foodorderapp.data.model.MenuItemDto
import com.example.foodorderapp.data.model.OrderRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodApiService {
    @GET("exec")
    suspend fun getMenu(): List<MenuItemDto>

    @POST("exec")
    suspend fun placeOrder(@Body order: OrderRequest): Map<String, String>
}
