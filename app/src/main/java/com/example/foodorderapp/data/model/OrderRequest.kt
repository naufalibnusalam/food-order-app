package com.example.foodorderapp.data.model

// Model untuk mengirim pesanan (POST)
data class OrderRequest(
    val customerEmail: String,
    val items: String,
    val totalPrice: Long
)
