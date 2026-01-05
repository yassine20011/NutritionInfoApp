# ProductInfoApp Backend

A Node.js/Express backend for a Yuka-style product scanner app. Provides health scoring, nutrition data, additive analysis, powered by a local Open Food Facts database.

## Features

- 🔍 **Barcode Lookup** — Search products by barcode
- 🔎 **Product Search** — Search products by name or brand
- 📊 **Health Scoring** — NutriScore-inspired 0-100 health score calculation
- ⚠️ **Additive Analysis** — Risk level assessment for food additives
- 🥗 **Healthier Alternatives** — Find better products in the same category
- 🌿 **Organic Detection** — Identifies organic products

## Tech Stack

- **Runtime:** Node.js
- **Framework:** Express.js
- **Database:** MongoDB (Mongoose ODM)
- **Data Source:** Open Food Facts database (local import)

## Quick Start

### Prerequisites

- Node.js 18+
- MongoDB 6.0+
- Open Food Facts JSONL database file

### Installation

```bash
# Install dependencies
npm install

# Configure environment
cp .env.example .env
# Edit .env with your MongoDB URI

# Start development server
npm run dev

# Or start production server
npm start
```

### Importing Open Food Facts Data

1. Download the [Open Food Facts JSONL export](https://world.openfoodfacts.org/data)
2. Place file at `~/Downloads/openfoodfacts-products.jsonl`
3. Run the import:

```bash
npm run import
```

The script imports products with nutrition data, additives, and calculates health scores.

### Environment Variables

```env
MONGO_URI=mongodb://localhost:27017/productinfo_db
PORT=3000
```

## API Endpoints

### Products

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/products` | List all products (paginated) |
| GET | `/products/:id` | Get product by ID |
| GET | `/products/barcode/:code` | Get product by barcode |
| GET | `/products/search/:query` | Search products by name/brand |
| GET | `/products/:id/alternatives` | Get healthier alternatives |
| POST | `/products` | Create product |
| PUT | `/products/:id` | Update product |
| DELETE | `/products/:id` | Delete product |

### Response Format

```json
{
  "_id": "507f1f77bcf86cd799439011",
  "barcode": "3017620422003",
  "name": "Nutella",
  "brand": "Ferrero",
  "score": 14,
  "nutriScore": "E",
  "isOrganic": false,
  "nutrition": {
    "calories": 539,
    "sugar": 56.3,
    "fat": 30.9,
    "saturatedFat": 10.6,
    "salt": 0.107,
    "protein": 6.3,
    "fiber": 0
  },
  "additives": [
    { "code": "E322", "name": "Lecithin", "riskLevel": "none" }
  ]
}
```

## Health Score Algorithm

The health score (0-100) is calculated using:

- **60%** Nutritional quality (sugar, fat, salt, fiber, protein)
- **30%** Additive penalties (based on risk levels)
- **10%** Organic bonus

```
Score Categories:
75-100: Excellent (Green)
50-74:  Good (Light Green)
25-49:  Poor (Orange)
0-24:   Bad (Red)
```

## Project Structure

```
backend/
├── app.js                       # Entry point
├── config/
│   └── database.js              # MongoDB connection
├── controllers/
│   └── productController.js     # API handlers
├── models/
│   ├── Product.js               # Mongoose product schema
│   └── index.js                 # Model exports
├── routes/
│   └── productRoutes.js         # Route definitions
├── scripts/
│   ├── importOFF.js             # Open Food Facts import
│   └── recalculateScores.js     # Batch score recalculation
└── utils/
    └── scoreCalculator.js       # Health score algorithm
```

