package com.example.foodorderapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodorderapp.data.local.menuItems
import com.example.foodorderapp.ui.components.MenuCard
import com.example.foodorderapp.ui.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, viewModel: CartViewModel) {
    val cartItems = viewModel.cartItems
    val totalPrice = viewModel.getTotalPrice()
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage by viewModel.errorMessage

    // Menampilkan Snackbar jika ada error
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.errorMessage.value = null // Reset error setelah ditampilkan
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Keranjang Saya", fontWeight = FontWeight.SemiBold, color = TextGray) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextGray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("menu") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PastelBlue,
                        selectedTextColor = PastelBlue,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray,
                        indicatorColor = SoftGray
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Stay here */ },
                    icon = {
                        BadgedBox(badge = {
                            if (viewModel.getTotalItems() > 0) {
                                Badge(containerColor = Color.Red) { Text("${viewModel.getTotalItems()}", color = Color.White) }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    },
                    label = { Text("Cart") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PastelBlue,
                        selectedTextColor = PastelBlue,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray,
                        indicatorColor = SoftGray
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("order_status") },
                    icon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = "Order") },
                    label = { Text("Order") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PastelBlue,
                        selectedTextColor = PastelBlue,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray,
                        indicatorColor = SoftGray
                    )
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(SoftGray)
        ) {
            if (cartItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Keranjangmu masih kosong 🛒", color = TextGray)
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(cartItems.entries.toList()) { (id, qty) ->
                            val item = menuItems.find { it.id == id }
                            if (item != null) {
                                MenuCard(
                                    item = item,
                                    quantity = qty,
                                    onUpdateQuantity = { newQty -> viewModel.updateQuantity(id, newQty) }
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Pembayaran", fontWeight = FontWeight.Bold, color = TextGray)
                                Text("Rp $totalPrice", fontWeight = FontWeight.Bold, color = Color.DarkGray, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    // Kirim pesanan ke API Google Sheets
                                    viewModel.placeOrder("customer@example.com") {
                                        navController.navigate("order_status")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PastelGreen),
                                enabled = !viewModel.isLoading.value
                            ) {
                                if (viewModel.isLoading.value) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                                } else {
                                    Text("Pesan Sekarang", color = Color(0xFF43A047), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
