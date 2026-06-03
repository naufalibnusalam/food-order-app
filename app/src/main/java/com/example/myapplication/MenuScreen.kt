package com.example.myapplication

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

// Pastel Palette Definitions
val PastelBlue = Color(0xFFAEC6CF)
val PastelGreen = Color(0xFFB2E2D2)
val PastelOrange = Color(0xFFFFD1B2)
val SoftGray = Color(0xFFF5F5F5)
val TextGray = Color(0xFF757575)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavController, viewModel: CartViewModel) {
    var selectedCategory by remember { mutableStateOf("Semua") }
    val categories = listOf("Semua", "Makanan", "Minuman", "Cemilan")
    
    // Ambil data dari API saat layar pertama kali dibuka
    LaunchedEffect(Unit) {
        if (viewModel.menuList.isEmpty()) {
            viewModel.fetchMenu()
        }
    }

    // Gunakan data dari API jika tersedia, jika tidak gunakan data lokal (fallback)
    val displayMenu = if (viewModel.menuList.isNotEmpty()) viewModel.menuList else menuItems
    
    val filteredMenu = if (selectedCategory == "Semua") {
        displayMenu
    } else {
        displayMenu.filter { it.category == selectedCategory }
    }

    val totalItems = viewModel.getTotalItems()
    val totalPrice = viewModel.getTotalPrice()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Order App", fontWeight = FontWeight.SemiBold, color = TextGray) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextGray
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Tetap di Menu */ },
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
        },
        floatingActionButton = {
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
        Box(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoftGray)
            ) {
                // Category Filter
                LazyRow(
                    modifier = Modifier.padding(vertical = 16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category, fontWeight = if(selectedCategory == category) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PastelBlue,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = TextGray
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedCategory == category,
                                borderColor = if(selectedCategory == category) Color.Transparent else Color(0xFFE0E0E0),
                                borderWidth = 1.dp,
                                selectedBorderColor = Color.Transparent,
                                selectedBorderWidth = 0.dp
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                if (viewModel.isLoading.value && viewModel.menuList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PastelBlue)
                    }
                } else {
                    LazyColumn(
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

@Composable
fun MenuCard(item: MenuItem, quantity: Int, onUpdateQuantity: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image with AsyncImage
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(item.color),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                if (item.isPopular) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PastelOrange.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "Populer",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE67E22)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(item.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(14.dp))
                    Text(" ${item.rating}", fontSize = 12.sp, color = TextGray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Rp ${item.price}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF455A64))
            }

            // Minimalist Quantity Control
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftGray)
                    .padding(4.dp)
            ) {
                if (quantity > 0) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onUpdateQuantity(quantity - 1) }
                    )
                    Text(
                        "$quantity",
                        modifier = Modifier.padding(horizontal = 10.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PastelBlue)
                        .clickable { onUpdateQuantity(quantity + 1) }
                        .padding(4.dp)
                )
            }
        }
    }
}
