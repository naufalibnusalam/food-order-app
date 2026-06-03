package com.example.foodorderapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val pastelBlue = Color(0xFFAEC6CF)
    val pastelGreen = Color(0xFFB2E2D2)

    // State untuk memicu animasi saat layar pertama kali dimuat
    var startAnimation by remember { mutableStateOf(false) }

    // Animasi muncul perlahan (fade in)
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )

    // Animasi perbesaran logo (scaling)
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1.2f else 0.8f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = FastOutSlowInEasing
        ),
        label = "scale"
    )

    // Logika jeda 2.5 detik sebelum pindah otomatis ke layar Login
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500L)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true } // Hapus splash dari history backstack
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, pastelBlue.copy(alpha = 0.2f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Lingkaran dekoratif di belakang logo
        Box(
            modifier = Modifier
                .size(300.dp)
                .scale(scaleAnim.value)
                .alpha(alphaAnim.value * 0.1f)
                .background(pastelGreen, CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scaleAnim.value * 0.8f)
                .alpha(alphaAnim.value)
        ) {
            // Ikon Restoran (Logo)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(pastelBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nama Branding
            Text(
                text = "Food Order",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF455A64),
                letterSpacing = 2.sp
            )

            Text(
                text = "Enjoy your meal",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF757575).copy(alpha = 0.8f)
            )
        }

        // Teks loading di bagian bawah
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .alpha(alphaAnim.value)
        ) {
            Text(
                text = "Loading Deliciousness...",
                fontSize = 12.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
