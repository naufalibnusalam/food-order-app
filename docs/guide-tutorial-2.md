# Tutorial 2: Integrasi Google Sheets sebagai Database Dinamis (API)

Tutorial ini akan membimbing Anda cara menggunakan **Google Sheets** sebagai backend untuk aplikasi "Nusantara Bite". Kita akan membuat API untuk mengambil data menu (**GET**) dan menyimpan pesanan (**POST**).

---

## 1. Persiapan Google Sheets

1.  Buat Google Sheet baru dengan nama "Database Nusantara Bite".
2.  Ubah nama Sheet pertama menjadi `Menu`. Buat header di baris pertama:
    - `id`, `name`, `price`, `category`, `rating`, `isPopular`, `color`, `imageUrl`
3.  Isi beberapa data contoh di bawahnya.
    - *Tips*: Untuk kolom `color`, gunakan kode hex seperti `0xFFFFECB3`.
4.  Buat Sheet kedua dengan nama `Orders`. Buat header:
    - `orderId`, `customerEmail`, `items`, `totalPrice`, `timestamp`

---

## 2. Membuat Google Apps Script (Backend API)

1.  Di Google Sheets, klik **Extensions** > **Apps Script**.
2.  Hapus kode yang ada dan tempelkan kode berikut:

```javascript
function doGet(e) {
  var sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("Menu");
  var data = sheet.getDataRange().getValues();
  var header = data[0];
  var rows = data.slice(1);
  
  var result = rows.map(row => {
    var obj = {};
    header.forEach((key, i) => obj[key] = row[i]);
    return obj;
  });
  
  return ContentService.createTextOutput(JSON.stringify(result))
    .setMimeType(ContentService.MimeType.JSON);
}

function doPost(e) {
  var sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("Orders");
  var postData = JSON.parse(e.postData.contents);
  
  sheet.appendRow([
    "ORD-" + new Date().getTime(),
    postData.customerEmail,
    postData.items,
    postData.totalPrice,
    new Date().toLocaleString()
  ]);
  
  return ContentService.createTextOutput(JSON.stringify({ "status": "success" }))
    .setMimeType(ContentService.MimeType.JSON);
}
```

3.  Klik **Deploy** > **New Deployment**.
4.  Select type: **Web App**.
5.  Description: "Food Order API".
6.  Execute as: **Me**.
7.  Who has access: **Anyone** (Penting agar aplikasi bisa akses tanpa login Google).
8.  Klik **Deploy** dan salin **Web App URL** (Simpan URL ini).

---

## 3. Konfigurasi Android (Retrofit)

### `app/build.gradle.kts`
Tambahkan library Retrofit untuk koneksi API:

```kotlin
dependencies {
    // Retrofit & GSON Converter
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}
```

### Membuat Interface API (`ApiService.kt`)

```kotlin
package com.example.myapplication

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Model untuk data Order yang akan dikirim
data class OrderRequest(
    val customerEmail: String,
    val items: String,
    val totalPrice: Long
)

interface FoodApiService {
    @GET("exec") // Endpoint Apps Script biasanya berakhiran /exec
    suspend fun getMenu(): List<MenuItem>

    @POST("exec")
    suspend fun placeOrder(@Body order: OrderRequest): Map<String, String>
}
```

### Inisialisasi Retrofit

```kotlin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val retrofit = Retrofit.Builder()
    .baseUrl("URL_WEB_APP_ANDA_DI_SINI/") // Ganti dengan URL dari Google Apps Script
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val foodApi = retrofit.create(FoodApiService::class.java)
```

---

## 4. Cara Penggunaan di Aplikasi

### Mengambil Data Menu (GET)
Di `MenuScreen.kt`, Anda bisa memanggil API saat layar dibuka:

```kotlin
var menuItemsFromApi by remember { mutableStateOf<List<MenuItem>>(emptyList()) }

LaunchedEffect(Unit) {
    try {
        menuItemsFromApi = foodApi.getMenu()
    } catch (e: Exception) {
        // Handle error (koneksi gagal, dll)
    }
}
```

### Mengirim Pesanan (POST)
Saat tombol "Pesan Sekarang" di klik:

```kotlin
val orderData = OrderRequest(
    customerEmail = "user@example.com",
    items = cartItems.toString(),
    totalPrice = totalPrice
)

scope.launch {
    try {
        foodApi.placeOrder(orderData)
        // Navigasi ke OrderStatus jika berhasil
    } catch (e: Exception) {
        // Tampilkan pesan error
    }
}
```

---

## Kesimpulan
Dengan integrasi ini, Anda tidak perlu lagi mengubah kode aplikasi hanya untuk menambah menu baru atau mengubah harga. Cukup edit Google Sheets Anda, dan aplikasi akan otomatis terupdate!
