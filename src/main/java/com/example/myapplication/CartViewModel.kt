package com.example.myapplication

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel

class CartViewModel : ViewModel() {
    val cartItems = mutableStateMapOf<Int, Int>()

    fun updateQuantity(itemId: Int, newQty: Int) {
        if (newQty > 0) {
            cartItems[itemId] = newQty
        } else {
            cartItems.remove(itemId)
        }
    }

    fun getTotalItems(): Int = cartItems.values.sum()

    fun getTotalPrice(): Long {
        return cartItems.entries.sumOf { (id, qty) ->
            menuItems.find { it.id == id }?.price?.times(qty) ?: 0L
        }
    }

    fun clearCart() {
        cartItems.clear()
    }
}
