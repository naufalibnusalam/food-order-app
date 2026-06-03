package com.example.foodorderapp.model

import androidx.compose.ui.graphics.Color

// Model data untuk satu item menu
data class MenuItem(
    val id: Int,
    val name: String,
    val price: Long,
    val category: String,
    val rating: Double,
    val isPopular: Boolean,
    val color: Color, // Warna latar belakang kartu menu
    val imageUrl: String // URL foto makanan dari Unsplash
)
