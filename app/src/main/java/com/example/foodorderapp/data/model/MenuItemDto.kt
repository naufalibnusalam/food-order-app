package com.example.foodorderapp.data.model

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
