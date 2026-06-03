package com.example.myapplication

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderStatusScreen(navController: NavController) {
    var currentStep by remember { mutableIntStateOf(1) }
    val softGray = Color(0xFFF5F5F5)
    val textGray = Color(0xFF757575)
    val pastelBlue = Color(0xFFAEC6CF)
    val pastelGreen = Color(0xFFB2E2D2)
    val pastelOrange = Color(0xFFFFD1B2)
    val pastelYellow = Color(0xFFFFF9C4)

    LaunchedEffect(Unit) {
        while (currentStep < 4) {
            delay(5000)
            currentStep++
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lacak Pesanan", fontWeight = FontWeight.SemiBold, color = textGray) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = textGray)
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
                        selectedIconColor = pastelBlue,
                        selectedTextColor = pastelBlue,
                        unselectedIconColor = textGray,
                        unselectedTextColor = textGray,
                        indicatorColor = softGray
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("cart") },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
                    label = { Text("Cart") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = pastelBlue,
                        selectedTextColor = pastelBlue,
                        unselectedIconColor = textGray,
                        unselectedTextColor = textGray,
                        indicatorColor = softGray
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Stay here */ },
                    icon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = "Order") },
                    label = { Text("Order") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = pastelBlue,
                        selectedTextColor = pastelBlue,
                        unselectedIconColor = textGray,
                        unselectedTextColor = textGray,
                        indicatorColor = softGray
                    )
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, softGray)
                    )
                )
        ) {
            // Hero Status Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(pastelBlue, pastelBlue.copy(alpha = 0.7f))
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when(currentStep) {
                                1 -> "Pesanan Diterima"
                                2 -> "Sedang Dimasak"
                                3 -> "Sedang Diantar"
                                else -> "Selesai Diantar"
                            },
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Estimasi: 15-20 Menit",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                    Icon(
                        imageVector = when(currentStep) {
                            1 -> Icons.Default.Receipt
                            2 -> Icons.Default.Restaurant
                            3 -> Icons.Default.DeliveryDining
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Tracking Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Detail Pengiriman", fontWeight = FontWeight.Bold, color = Color.DarkGray, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(24.dp))

                    TrackingItem(
                        title = "Pesanan Dikonfirmasi",
                        time = "12:30",
                        isCompleted = currentStep >= 1,
                        isCurrent = currentStep == 1,
                        icon = Icons.Default.Check,
                        color = pastelBlue
                    )
                    TrackingLine(isCompleted = currentStep > 1)
                    TrackingItem(
                        title = "Koki Sedang Menyiapkan",
                        time = "12:35",
                        isCompleted = currentStep >= 2,
                        isCurrent = currentStep == 2,
                        icon = Icons.Default.Kitchen,
                        color = pastelYellow
                    )
                    TrackingLine(isCompleted = currentStep > 2)
                    TrackingItem(
                        title = "Driver Menuju Lokasimu",
                        time = "12:45",
                        isCompleted = currentStep >= 3,
                        isCurrent = currentStep == 3,
                        icon = Icons.Default.Moped,
                        color = pastelOrange
                    )
                    TrackingLine(isCompleted = currentStep > 3)
                    TrackingItem(
                        title = "Pesanan Sampai!",
                        time = "12:55",
                        isCompleted = currentStep >= 4,
                        isCurrent = currentStep == 4,
                        icon = Icons.Default.Home,
                        color = pastelGreen
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Action
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                if (currentStep < 4) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = pastelGreen.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFF43A047))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Hampir sampai! Siapkan perutmu ✨",
                                fontSize = 14.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = { navController.navigate("menu") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = pastelGreen)
                    ) {
                        Text("Pesan Lagi", color = Color(0xFF43A047), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun TrackingItem(
    title: String,
    time: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    icon: ImageVector,
    color: Color
) {
    val scale by animateFloatAsState(if (isCurrent) 1.1f else 1f, label = "scale")

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .scale(scale)
                .background(
                    if (isCompleted) color else Color(0xFFF0F0F0),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (isCompleted) Color.White else Color.LightGray
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                color = if (isCompleted) Color.DarkGray else Color.LightGray
            )
            if (isCompleted) {
                Text(text = time, fontSize = 12.sp, color = Color.Gray)
            }
        }
        if (isCurrent) {
            Text(
                "Proses",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier
                    .background(color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun TrackingLine(isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .padding(start = 17.dp)
            .width(2.dp)
            .height(24.dp)
            .background(if (isCompleted) Color(0xFFB2E2D2) else Color(0xFFEEEEEE))
    )
}
