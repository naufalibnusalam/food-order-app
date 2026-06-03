package com.example.foodorderapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.navigation.NavController
import com.example.foodorderapp.ui.components.MenuCard
import com.example.foodorderapp.ui.viewmodel.CartViewModel

// Definisi warna pastel global untuk layar Menu
val PastelBlue = Color(0xFFAEC6CF)
val PastelGreen = Color(0xFFB2E2D2)
val SoftGray = Color(0xFFF5F5F5)
val TextGray = Color(0xFF757575)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavController, viewModel: CartViewModel) {
    // Panggil fetchMenu saat layar pertama kali dibuka
    LaunchedEffect(Unit) {
        viewModel.fetchMenu()
    }

    // Ambil data dari ViewModel
    val menuList = viewModel.menuList
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    // State untuk kategori yang sedang dipilih
    var selectedCategory by remember { mutableStateOf("Semua") }
    val categories = listOf("Semua", "Makanan", "Minuman", "Cemilan")

    // Memfilter daftar menu berdasarkan kategori yang dipilih pengguna
    val filteredMenu = if (selectedCategory == "Semua") {
        menuList
    } else {
        menuList.filter { it.category == selectedCategory }
    }

    // Mengambil data total item dan harga dari ViewModel
    val totalItems = viewModel.getTotalItems()
    val totalPrice = viewModel.getTotalPrice()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Order", fontWeight = FontWeight.SemiBold, color = TextGray) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextGray
                )
            )
        },
        bottomBar = {
            // Bar navigasi bawah (Home, Cart, Order)
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Tetap di halaman Home */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PastelBlue,
                        selectedTextColor = PastelBlue,
                        indicatorColor = SoftGray
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("cart") },
                    icon = {
                        BadgedBox(badge = {
                            if (totalItems > 0) {
                                Badge(containerColor = Color.Red) { Text("$totalItems", color = Color.White) }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    },
                    label = { Text("Cart") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("order_status") },
                    icon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = "Order") },
                    label = { Text("Order") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
            }
        },
        floatingActionButton = {
            // Tombol checkout yang muncul hanya jika ada barang di keranjang
            AnimatedVisibility(
                visible = totalItems > 0,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate("cart") },
                    containerColor = PastelGreen,
                    contentColor = Color(0xFF43A047),
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    text = { Text("Checkout • Rp $totalPrice", fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(20.dp)
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
            // Row horizontal untuk daftar filter kategori
            LazyRow(
                modifier = Modifier.padding(vertical = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PastelBlue,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading && menuList.isEmpty()) {
                    // Tampilkan loading spinner saat pertama kali ambil data
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PastelBlue
                    )
                } else if (errorMessage != null && menuList.isEmpty()) {
                    // Tampilkan pesan error jika gagal ambil data
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(errorMessage!!, color = Color.Red, modifier = Modifier.padding(16.dp))
                        Button(onClick = { viewModel.fetchMenu() }, colors = ButtonDefaults.buttonColors(containerColor = PastelBlue)) {
                            Text("Coba Lagi")
                        }
                    }
                } else {
                    // Daftar vertikal kartu menu
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(filteredMenu) { item ->
                            MenuCard(
                                item = item,
                                quantity = viewModel.cartItems[item.id] ?: 0,
                                onUpdateQuantity = { newQty ->
                                    viewModel.updateQuantity(item.id, newQty)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
