package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodAppNavigation()
        }
    }
}

@Composable
fun FoodAppNavigation() {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()
    
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("menu") { MenuScreen(navController, cartViewModel) }
        composable("cart") { CartScreen(navController, cartViewModel) }
        composable("order_status") { OrderStatusScreen(navController) }
    }
}