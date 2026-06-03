# Tutorial Lengkap: Membangun Aplikasi Food Order "Nusantara Bite"

Tutorial ini menyediakan seluruh kode sumber yang diperlukan untuk membangun aplikasi pemesanan makanan modern menggunakan **Jetpack Compose**. Kode di bawah ini telah dilengkapi dengan komentar untuk membantu Anda memahami fungsi dari setiap bagian.

---

## 1. Konfigurasi Gradle & Manifest

### `app/build.gradle.kts`
Tambahkan dependensi berikut di dalam blok `dependencies { ... }`:

```kotlin
dependencies {
    // Library untuk navigasi antar halaman (Jetpack Navigation)
    implementation("androidx.navigation:navigation-compose:2.7.7")
    // Kumpulan ikon tambahan dari Material Design
    implementation("androidx.compose.material:material-icons-extended")
    // Library Coil untuk menampilkan gambar secara efisien dari URL/Internet
    implementation("io.coil-kt:coil-compose:2.7.0")
    // Library ViewModel untuk mengelola data (state) agar tidak hilang saat rotasi layar atau pindah halaman
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
}
```

### `gradle/libs.versions.toml`
Tambahkan kode berikut:

```kotlin
...material
composeMaterialIconsExtended = "1.7.8"
navigationCompose = "2.8.8"
coilCompose = "2.7.0"
lifecycleViewmodelCompose = "2.8.7"

...[libraries]
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended", version.ref = "composeMaterialIconsExtended" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coilCompose" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleViewmodelCompose" }
```

### `AndroidManifest.xml`
Tambahkan izin Internet tepat di atas tag `<application>` agar aplikasi bisa mengambil gambar dari Unsplash:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 2. Kode Sumber Lengkap (`java/com/example/myapplication`)

### 2.1 `MenuData.kt`
Mendefinisikan model data dan daftar produk makanan/minuman yang tersedia.

```kotlin
package com.example.myapplication

import androidx.compose.ui.graphics.Color

// Model data untuk satu item menu
data class MenuItem(
    val id: Int,
    val name: String,
    val price: Long,
    val category: String,
    val rating: Double,
    val isPopular: Boolean,
    val color: Color, // Warna latar belakang kartu menu
    val imageUrl: String // URL foto makanan dari Unsplash
)

// Daftar menu (database statis) untuk ditampilkan di aplikasi
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
```

### 2.2 `CartViewModel.kt`
Mengelola data keranjang belanja yang dapat diakses oleh semua halaman.

```kotlin
package com.example.myapplication

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel

class CartViewModel : ViewModel() {
    // mutableStateMapOf digunakan agar UI otomatis update saat isi keranjang berubah
    // Key: ID Menu (Int), Value: Jumlah pesanan (Int)
    val cartItems = mutableStateMapOf<Int, Int>()

    // Menambah atau mengurangi jumlah item dalam keranjang
    fun updateQuantity(itemId: Int, newQty: Int) {
        if (newQty > 0) {
            cartItems[itemId] = newQty
        } else {
            cartItems.remove(itemId) // Hapus jika jumlah jadi 0
        }
    }

    // Menghitung total seluruh item yang ada di keranjang
    fun getTotalItems(): Int = cartItems.values.sum()

    // Menghitung total harga pembayaran
    fun getTotalPrice(): Long {
        return cartItems.entries.sumOf { (id, qty) ->
            menuItems.find { it.id == id }?.price?.times(qty) ?: 0L
        }
    }

    // Mengosongkan isi keranjang
    fun clearCart() {
        cartItems.clear()
    }
}
```

### 2.3 `SplashScreen.kt`
Halaman pembuka dengan animasi scaling dan gradient pastel yang lembut.

```kotlin
package com.example.myapplication

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
```

### 2.4 `LoginScreen.kt`
Halaman otentikasi dengan nuansa Pastel Blue.

```kotlin
package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {
    // State untuk menyimpan input teks pengguna
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    val pastelBlue = Color(0xFFAEC6CF)
    val textGray = Color(0xFF757575)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, pastelBlue.copy(alpha = 0.1f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo kecil di atas form
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(pastelBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Welcome Back!",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF455A64)
            )
            
            Text(
                "Login to your account",
                fontSize = 14.sp,
                color = textGray,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Input Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = pastelBlue,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Input Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = pastelBlue,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Tombol Masuk
            Button(
                onClick = { navController.navigate("menu") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = pastelBlue)
            ) {
                Text("Masuk", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tombol Navigasi ke halaman Register
            TextButton(onClick = { navController.navigate("register") }) {
                Text(
                    "Belum punya akun? Registrasi di sini",
                    color = pastelBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
```

### 2.5 `RegisterScreen.kt`
Halaman pendaftaran pengguna baru dengan nuansa Pastel Green.

```kotlin
package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun RegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    val pastelGreen = Color(0xFFB2E2D2)
    val textGray = Color(0xFF757575)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, pastelGreen.copy(alpha = 0.1f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(pastelGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF455A64)
            )
            
            Text(
                "Sign up to get started",
                fontSize = 14.sp,
                color = textGray,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Input Nama Lengkap
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = pastelGreen,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Input Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = pastelGreen,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Input Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = pastelGreen,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Tombol Daftar
            Button(
                onClick = { navController.navigate("menu") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = pastelGreen)
            ) {
                Text("Daftar & Masuk", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF43A047))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Kembali ke halaman Login
            TextButton(onClick = { navController.popBackStack() }) {
                Text(
                    "Sudah punya akun? Masuk di sini",
                    color = Color(0xFF43A047),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
```

### 2.6 `MenuScreen.kt`
Halaman utama yang menampilkan daftar menu dengan fitur kategori dan keranjang belanja.

```kotlin
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

// Definisi warna pastel global untuk layar Menu
val PastelBlue = Color(0xFFAEC6CF)
val PastelGreen = Color(0xFFB2E2D2)
val PastelOrange = Color(0xFFFFD1B2)
val SoftGray = Color(0xFFF5F5F5)
val TextGray = Color(0xFF757575)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavController, viewModel: CartViewModel) {
    // State untuk kategori yang sedang dipilih
    var selectedCategory by remember { mutableStateOf("Semua") }
    val categories = listOf("Semua", "Makanan", "Minuman", "Cemilan")
    
    // Memfilter daftar menu berdasarkan kategori yang dipilih pengguna
    val filteredMenu = if (selectedCategory == "Semua") {
        menuItems
    } else {
        menuItems.filter { it.category == selectedCategory }
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

            // Daftar vertikal kartu menu
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

// Komponen kartu untuk satu item menu
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
            // Menampilkan gambar dari internet menggunakan library Coil (AsyncImage)
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

            // Detail informasi makanan
            Column(modifier = Modifier.weight(1f)) {
                if (item.isPopular) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PastelOrange.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Populer", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE67E22))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(item.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFD54F), modifier = Modifier.size(14.dp))
                    Text(" ${item.rating}", fontSize = 12.sp, color = TextGray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Rp ${item.price}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF455A64))
            }

            // Kontrol jumlah pesanan (+/-)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(SoftGray).padding(4.dp)
            ) {
                if (quantity > 0) {
                    Icon(
                        Icons.Default.Remove,
                        null,
                        tint = TextGray,
                        modifier = Modifier.size(24.dp).clickable { onUpdateQuantity(quantity - 1) }
                    )
                    Text("$quantity", modifier = Modifier.padding(horizontal = 10.dp), fontWeight = FontWeight.Bold)
                }
                Icon(
                    Icons.Default.Add,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp).clip(RoundedCornerShape(8.dp)).background(PastelBlue).clickable { onUpdateQuantity(quantity + 1) }.padding(4.dp)
                )
            }
        }
    }
}
```

### 2.7 `CartScreen.kt`
Halaman untuk meninjau pesanan di keranjang sebelum melakukan pemesanan.

```kotlin
package com.example.myapplication

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, viewModel: CartViewModel) {
    val cartItems = viewModel.cartItems
    val totalPrice = viewModel.getTotalPrice()

    Scaffold(
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
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("menu") },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Tetap di sini */ },
                    icon = { 
                        BadgedBox(badge = { 
                            if (viewModel.getTotalItems() > 0) {
                                Badge(containerColor = Color.Red) { Text("${viewModel.getTotalItems()}", color = Color.White) }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, null)
                        }
                    },
                    label = { Text("Cart") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = PastelBlue, selectedTextColor = PastelBlue)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("order_status") },
                    icon = { Icon(Icons.AutoMirrored.Filled.Assignment, null) },
                    label = { Text("Order") }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(SoftGray)) {
            if (cartItems.isEmpty()) {
                // Tampilan jika keranjang kosong
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Keranjangmu masih kosong 🛒", color = TextGray)
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    // Daftar item yang ada di keranjang
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

                    // Kartu ringkasan total harga di bagian bawah
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Pembayaran", fontWeight = FontWeight.Bold, color = TextGray)
                                Text("Rp $totalPrice", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Spacer(Modifier.height(20.dp))
                            Button(
                                onClick = { navController.navigate("order_status") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PastelGreen)
                            ) {
                                Text("Pesan Sekarang", color = Color(0xFF43A047), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
```

### 2.8 `OrderStatusScreen.kt`
Halaman untuk melacak proses pesanan mulai dari diterima hingga sampai di tujuan.

```kotlin
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
    // State untuk mensimulasikan proses pesanan (1=Diterima, 2=Dimasak, dst)
    var currentStep by remember { mutableIntStateOf(1) }
    
    val softGray = Color(0xFFF5F5F5)
    val textGray = Color(0xFF757575)
    val pastelBlue = Color(0xFFAEC6CF)
    val pastelGreen = Color(0xFFB2E2D2)
    val pastelOrange = Color(0xFFFFD1B2)
    val pastelYellow = Color(0xFFFFF9C4)

    // Simulasi update status pesanan setiap 5 detik secara otomatis
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = textGray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("menu") },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("cart") },
                    icon = { Icon(Icons.Default.ShoppingCart, null) },
                    label = { Text("Cart") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Stay */ },
                    icon = { Icon(Icons.AutoMirrored.Filled.Assignment, null) },
                    label = { Text("Order") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = pastelBlue, selectedTextColor = pastelBlue)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .background(Brush.verticalGradient(listOf(Color.White, softGray)))
        ) {
            // Bagian Hero (Status Utama)
            Box(
                modifier = Modifier.fillMaxWidth().padding(24.dp).clip(RoundedCornerShape(32.dp))
                    .background(Brush.horizontalGradient(listOf(pastelBlue, pastelBlue.copy(0.7f))))
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
                            color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold
                        )
                        Text("Estimasi: 15-20 Menit", color = Color.White.copy(0.8f), fontSize = 14.sp)
                    }
                    Icon(
                        imageVector = when(currentStep) {
                            1 -> Icons.Default.Receipt
                            2 -> Icons.Default.Restaurant
                            3 -> Icons.Default.DeliveryDining
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Kartu Detail Pelacakan (Tracking Timeline)
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Detail Pengiriman", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(Modifier.height(24.dp))

                    TrackingItem("Pesanan Dikonfirmasi", "12:30", currentStep >= 1, currentStep == 1, Icons.Default.Check, pastelBlue)
                    TrackingLine(currentStep > 1)
                    TrackingItem("Koki Menyiapkan", "12:35", currentStep >= 2, currentStep == 2, Icons.Default.Kitchen, pastelYellow)
                    TrackingLine(currentStep > 2)
                    TrackingItem("Driver Menuju Lokasimu", "12:45", currentStep >= 3, currentStep == 3, Icons.Default.Moped, pastelOrange)
                    TrackingLine(currentStep > 3)
                    TrackingItem("Pesanan Sampai!", "12:55", currentStep >= 4, currentStep == 4, Icons.Default.Home, pastelGreen)
                }
            }

            Spacer(Modifier.weight(1f))

            // Pesan interaktif di paling bawah
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                if (currentStep < 4) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = pastelGreen.copy(0.2f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Celebration, null, tint = Color(0xFF43A047))
                            Spacer(Modifier.width(12.dp))
                            Text("Hampir sampai! Siapkan perutmu ✨", color = Color(0xFF2E7D32), fontSize = 14.sp)
                        }
                    }
                } else {
                    Button(
                        onClick = { navController.navigate("menu") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = pastelGreen)
                    ) {
                        Text("Pesan Lagi", color = Color(0xFF43A047), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Komponen baris status di timeline pelacakan
@Composable
fun TrackingItem(title: String, time: String, isCompleted: Boolean, isCurrent: Boolean, icon: ImageVector, color: Color) {
    val scale by animateFloatAsState(if (isCurrent) 1.1f else 1f, label = "scale")

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(36.dp).scale(scale).background(if (isCompleted) color else Color(0xFFF0F0F0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, Modifier.size(18.dp), if (isCompleted) Color.White else Color.LightGray)
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium, color = if (isCompleted) Color.DarkGray else Color.LightGray)
            if (isCompleted) Text(time, fontSize = 12.sp, color = Color.Gray)
        }
        if (isCurrent) {
            Text("Proses", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color, modifier = Modifier.background(color.copy(0.2f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp))
        }
    }
}

// Komponen garis vertikal penghubung antar status
@Composable
fun TrackingLine(isCompleted: Boolean) {
    Box(modifier = Modifier.padding(start = 17.dp).width(2.dp).height(24.dp).background(if (isCompleted) Color(0xFFB2E2D2) else Color(0xFFEEEEEE)))
}
```

### 2.9 `MainActivity.kt`
Pusat kontrol navigasi dan inisialisasi aplikasi.

```kotlin
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
```
