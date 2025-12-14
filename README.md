# ProductInfoApp

A Yuka-style food product scanner that provides health scores, nutrition analysis, and additive risk assessment. Scan barcodes to make healthier food choices.

## 🏗️ Architecture

```
ProductInfoApp/
├── backend/       # Node.js + Express + MongoDB API
└── androidApp/    # Android Java MVVM client
```

## ✨ Features

| Feature | Description |
|---------|-------------|
| 📷 **Barcode Scanner** | ML Kit-powered barcode detection |
| 🎯 **Health Score** | 0-100 score with color-coded indicators |
| 🔤 **NutriScore** | A-E grade classification |
| 📊 **Nutrition Facts** | Calories, sugar, fat, protein, fiber breakdown |
| ⚠️ **Additive Warnings** | Risk level assessment (none → hazardous) |
| 🥗 **Alternatives** | Healthier product recommendations |
| 📜 **Scan History** | Track previously scanned products |
| ⭐ **Favorites** | Save preferred products locally |

## 🛠️ Tech Stack

### Backend
- **Runtime:** Node.js
- **Framework:** Express.js
- **Database:** MongoDB (Mongoose ODM)
- **Data Source:** Open Food Facts

### Android App
- **Language:** Java
- **Architecture:** MVVM
- **Camera:** CameraX + ML Kit
- **Network:** Retrofit2
- **Local DB:** Room

## 🚀 Quick Start

### 1. Start Backend

```bash
cd backend
npm install
cp .env.example .env  # Configure MONGO_URI
npm run dev
```

### 2. Import Data

Download [Open Food Facts JSONL](https://world.openfoodfacts.org/data) and run:

```bash
npm run import
```

### 3. Build Android App

```bash
cd androidApp
./gradlew assembleDebug
```

> Update `RetrofitClient.java` with your server IP

## 📖 Documentation

- [Backend README](./backend/README.md) — API endpoints, health score algorithm
- [Android README](./androidApp/README.md) — App setup, project structure

## 📄 License

MIT
