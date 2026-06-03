package com.example.myapplication

import androidx.compose.ui.graphics.Color

data class MenuItem(
    val id: Int,
    val name: String,
    val price: Long,
    val category: String,
    val rating: Double,
    val isPopular: Boolean,
    val color: Color,
    val imageUrl: String
)

val menuItems = listOf(
    MenuItem(1, "Nasi Goreng Spesial", 25000, "Makanan", 4.8, true, Color(0xFFFFECB3), "https://images.unsplash.com/photo-1603133872878-684f208fb84b?auto=format&fit=crop&w=200&q=80"),
    MenuItem(2, "Beef Burger Deluxe", 45000, "Makanan", 4.9, true, Color(0xFFFFCCBC), "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=200&q=80"),
    MenuItem(3, "Pepperoni Pizza", 65000, "Makanan", 4.7, false, Color(0xFFF8BBD0), "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=200&q=80"),
    MenuItem(4, "Ayam Bakar Madu", 35000, "Makanan", 4.9, false, Color(0xFFFFE0B2), "https://images.unsplash.com/photo-1598515214211-89d3c73ae83b?auto=format&fit=crop&w=200&q=80"),
    MenuItem(5, "Cireng Bumbu Rujak", 15000, "Cemilan", 4.7, false, Color(0xFFDCEDC8), "https://images.unsplash.com/photo-1541544741938-0af808871cc0?auto=format&fit=crop&w=200&q=80"),
    MenuItem(6, "Es Teh Manis", 5000, "Minuman", 4.5, false, Color(0xFFB3E5FC), "https://images.unsplash.com/photo-1556679343-c7306c1976bc?auto=format&fit=crop&w=200&q=80"),
    MenuItem(7, "Kopi Susu Gula Aren", 18000, "Minuman", 4.8, true, Color(0xFFD7CCC8), "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=200&q=80"),
    MenuItem(8, "Matcha Latte", 22000, "Minuman", 4.7, false, Color(0xFFC8E6C9), "https://images.unsplash.com/photo-1515823064-d6e0c04616a7?auto=format&fit=crop&w=200&q=80"),
    MenuItem(9, "Pisang Goreng Keju", 15000, "Cemilan", 4.6, false, Color(0xFFFFF9C4), "https://images.unsplash.com/photo-1590301157890-4810ed352733?auto=format&fit=crop&w=200&q=80"),
    MenuItem(10, "French Fries", 20000, "Cemilan", 4.5, false, Color(0xFFF0F4C3), "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?auto=format&fit=crop&w=200&q=80")
)
