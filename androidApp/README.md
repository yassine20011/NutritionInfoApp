# ProductInfoApp Android

A Yuka-style product scanner Android app. Scan barcodes to get health scores, nutrition facts, additive warnings, and healthier alternatives.

## Features

- 📷 **Barcode Scanner** — ML Kit barcode detection with CameraX
- 🎯 **Health Scoring** — Color-coded 0-100 health score
- 🔤 **NutriScore** — A-E grade display
- 📊 **Nutrition Facts** — Detailed breakdown with indicators
- ⚠️ **Additive Warnings** — Risk levels (none/limited/moderate/hazardous)
- 🥗 **Alternatives** — Healthier product suggestions
- 📜 **Scan History** — Track scanned products
- ⭐ **Favorites** — Save preferred products

## Screenshots

| Product List | Scanner | Product Detail | History |
|--------------|---------|----------------|---------|
| Two FABs: Scan + History | Camera with scan frame | Score circle + nutrition | Tabs: All / Favorites |

## Tech Stack

- **Language:** Java
- **Architecture:** MVVM
- **Camera:** CameraX
- **ML:** ML Kit Barcode Scanning
- **Network:** Retrofit2 + Gson
- **Database:** Room
- **UI:** Material Design Components

## Requirements

- Android 7.0+ (API 24)
- Camera permission

## Setup

### Prerequisites

- Android Studio Hedgehog+
- JDK 17
- Backend server running (see backend README)

### Configuration

Update the API base URL in `RetrofitClient.java`:

```java
private static final String BASE_URL = "http://YOUR_SERVER_IP:3000/";
```

> **Note:** Use `10.0.2.2` for Android emulator to connect to localhost

### Build

```bash
# From androidApp directory
./gradlew assembleDebug

# APK location
app/build/outputs/apk/debug/app-debug.apk
```

## Project Structure

```
app/src/main/java/com/example/productinfoapp/
├── data/
│   ├── api/
│   │   ├── ApiService.java         # Retrofit interface
│   │   └── RetrofitClient.java     # Network client
│   ├── local/
│   │   ├── AppDatabase.java        # Room database
│   │   ├── ProductDao.java         # Product DAO
│   │   ├── ProductEntity.java      # Product entity
│   │   ├── ScanHistoryDao.java     # History DAO
│   │   └── ScanHistoryEntity.java  # History entity
│   ├── model/
│   │   ├── Product.java            # Product model
│   │   ├── Nutrition.java          # Nutrition data
│   │   └── Additive.java           # Additive info
│   └── repository/
│       └── ProductRepository.java  # Data layer
├── ui/
│   ├── productlist/
│   │   ├── ProductListActivity.java
│   │   ├── ProductListViewModel.java
│   │   └── ProductAdapter.java
│   ├── productdetail/
│   │   ├── ProductDetailActivity.java
│   │   ├── ProductDetailViewModel.java
│   │   └── AlternativeProductAdapter.java
│   ├── scanner/
│   │   └── BarcodeScannerActivity.java
│   └── history/
│       ├── HistoryActivity.java
│       └── HistoryAdapter.java
```

## Key Dependencies

```gradle
// ML Kit Barcode
implementation 'com.google.mlkit:barcode-scanning:17.2.0'

// CameraX
implementation 'androidx.camera:camera-camera2:1.3.0'
implementation 'androidx.camera:camera-lifecycle:1.3.0'
implementation 'androidx.camera:camera-view:1.3.0'

// Retrofit
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// Room
implementation 'androidx.room:room-runtime:2.6.1'
annotationProcessor 'androidx.room:room-compiler:2.6.1'
```

## Usage

1. **Start backend server** (see backend README)
2. **Install app** on device/emulator
3. **Grant camera permission** when prompted
4. **Tap green scan button** to scan a barcode
5. **View product details** with health score
6. **Tap star** to add to favorites
7. **Tap blue history button** to view scan history

## Health Score Colors

| Score | Category | Color |
|-------|----------|-------|
| 75-100 | Excellent | 🟢 Green |
| 50-74 | Good | 🟢 Light Green |
| 25-49 | Poor | 🟠 Orange |
| 0-24 | Bad | 🔴 Red |
