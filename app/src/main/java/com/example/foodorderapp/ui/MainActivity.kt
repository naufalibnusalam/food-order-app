package com.example.foodorderapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodorderapp.ui.screens.*
import com.example.foodorderapp.ui.viewmodel.CartViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Menjalankan fungsi navigasi utama saat aplikasi dibuka
            FoodAppNavigation()
        }
    }
}

@Composable
fun FoodAppNavigation() {
    // NavController untuk mengatur perpindahan antar layar
    val navController = rememberNavController()
    // Menginisialisasi CartViewModel sekali saja untuk seluruh aplikasi
    val cartViewModel: CartViewModel = viewModel()

    // Mendefinisikan rute (destinasi) navigasi
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("menu") { MenuScreen(navController, cartViewModel) }
        composable("cart") { CartScreen(navController, cartViewModel) }
        composable("order_status") { OrderStatusScreen(navController) }
    }
}
