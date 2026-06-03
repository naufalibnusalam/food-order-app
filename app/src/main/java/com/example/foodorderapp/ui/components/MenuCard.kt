package com.example.foodorderapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.foodorderapp.model.MenuItem

val PastelBlue = Color(0xFFAEC6CF)
val PastelOrange = Color(0xFFFFD1B2)
val SoftGray = Color(0xFFF5F5F5)
val TextGray = Color(0xFF757575)

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
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftGray)
                    .padding(4.dp)
            ) {
                if (quantity > 0) {
                    Icon(
                        Icons.Default.Remove,
                        null,
                        tint = TextGray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onUpdateQuantity(quantity - 1) }
                    )
                    Text("$quantity", modifier = Modifier.padding(horizontal = 10.dp), fontWeight = FontWeight.Bold)
                }
                Icon(
                    Icons.Default.Add,
                    null,
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
