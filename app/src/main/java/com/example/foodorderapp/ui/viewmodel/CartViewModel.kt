package com.example.foodorderapp.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderapp.data.local.menuItems
import com.example.foodorderapp.data.model.OrderRequest
import com.example.foodorderapp.data.remote.RetrofitClient
import com.example.foodorderapp.model.MenuItem
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CartViewModel : ViewModel() {
    val cartItems = mutableStateMapOf<Int, Int>()

    // State untuk menampung data menu dari API
    val menuList = mutableStateListOf<MenuItem>()
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    // Fungsi untuk mengambil data menu dari Google Sheets
    fun fetchMenu() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val dtos = RetrofitClient.instance.getMenu()
                menuList.clear()
                menuList.addAll(dtos.map { dto ->
                    MenuItem(
                        id = dto.id,
                        name = dto.name,
                        price = dto.price,
                        category = dto.category,
                        rating = dto.rating,
                        isPopular = dto.isPopular,
                        color = try {
                            val colorString = if (dto.color.startsWith("0x")) {
                                dto.color.replace("0x", "#")
                            } else if (!dto.color.startsWith("#")) {
                                "#${dto.color}"
                            } else {
                                dto.color
                            }
                            Color(android.graphics.Color.parseColor(colorString))
                        } catch (e: Exception) {
                            Color.LightGray
                        },
                        imageUrl = dto.imageUrl
                    )
                })
                errorMessage.value = null
            } catch (e: Exception) {
                Log.e("CartViewModel", "Fetch Menu Error", e)
                errorMessage.value = "Gagal memuat menu: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // Fungsi untuk mengirim pesanan ke Google Sheets
    fun placeOrder(email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            val itemsString = cartItems.entries.joinToString { (id, qty) ->
                val name = menuList.find { it.id == id }?.name ?: menuItems.find { it.id == id }?.name ?: "Unknown"
                "$name x$qty"
            }
            val request = OrderRequest(
                customerEmail = email,
                items = itemsString,
                totalPrice = getTotalPrice()
            )
            try {
                val response = RetrofitClient.instance.placeOrder(request)
                Log.d("CartViewModel", "Order Success: $response")
                clearCart()
                onSuccess()
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("CartViewModel", "HTTP Error: ${e.code()}, Body: $errorBody")
                errorMessage.value = "Error Server (${e.code()})"
            } catch (e: IOException) {
                Log.e("CartViewModel", "Network Error", e)
                errorMessage.value = "Koneksi Internet Bermasalah"
            } catch (e: JsonSyntaxException) {
                Log.e("CartViewModel", "Parsing Error (Script redirecting to HTML?)", e)
                // Google Script sering redirect ke HTML sukses. Anggap sukses jika sampai sini.
                clearCart()
                onSuccess()
            } catch (e: Exception) {
                Log.e("CartViewModel", "Order Error", e)
                errorMessage.value = "Terjadi kesalahan: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

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
            val item = menuList.find { it.id == id } ?: menuItems.find { it.id == id }
            item?.price?.times(qty) ?: 0L
        }
    }

    fun clearCart() {
        cartItems.clear()
    }
}
