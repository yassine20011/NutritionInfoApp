# ProductInfoApp Backend

A Node.js/Express backend for a Yuka-style product scanner app. Provides health scoring, nutrition data, additive analysis, powered by a local OpenFoodFacts database.

## Features

- 🔍 **Barcode Lookup** - Search products by barcode from local database
- 🔎 **Product Search** - Search products by name or brand
- 📊 **Health Scoring** - NutriScore-inspired 0-100 health score calculation
- ⚠️ **Additive Analysis** - Risk level assessment for food additives
- 🥗 **Healthier Alternatives** - Find better products in the same category
- 🌿 **Organic Detection** - Identifies organic products

## Tech Stack

- **Runtime:** Node.js
- **Framework:** Express.js
- **Database:** MySQL with Sequelize ORM
- **Data Source:** OpenFoodFacts database (local import)

## Quick Start

### Prerequisites

- Node.js 16+
- MySQL 8.0+
- Docker (optional)
- OpenFoodFacts JSONL database file

### Installation

```bash
# Install dependencies
npm install

# Configure environment
cp .env.example .env
# Edit .env with your database credentials

# Start with Docker (recommended)
docker-compose up -d

# Or start manually
npm start
```

### Importing OpenFoodFacts Data

Download the OpenFoodFacts products database (JSONL format) and run the import script:

```bash
# Place your downloaded file at ~/Downloads/openfoodfacts-products.jsonl
node scripts/importOFF.js
```

The script will import products, nutrition data, and additives into your local MySQL database.

### Environment Variables

```env
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=productinfo_db
DB_PORT=3306
PORT=3000
```

## API Endpoints

### Products

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | List all products |
| GET | `/api/products/:id` | Get product by ID |
| GET | `/api/products/barcode/:code` | Get product by barcode |
| GET | `/api/products/search/:query` | Search products by name/brand |
| GET | `/api/products/:id/alternatives` | Get healthier alternatives |
| POST | `/api/products` | Create product |
| PUT | `/api/products/:id` | Update product |
| DELETE | `/api/products/:id` | Delete product |

### Response Format

```json
{
  "id": 1,
  "barcode": "3017620422003",
  "name": "Nutella",
  "brand": "Ferrero",
  "calculatedScore": 14,
  "scoreCategory": {
    "label": "Bad",
    "color": "#F44336"
  },
  "nutriScoreGrade": "E",
  "isOrganic": false,
  "Nutrition": {
    "calories": 539,
    "sugar": 56.3,
    "fat": 30.9,
    "saturatedFat": 10.6,
    "salt": 0.107,
    "protein": 6.3,
    "fiber": 0
  },
  "Additives": [
    {
      "code": "E322",
      "name": "Lecithin",
      "riskLevel": "none"
    }
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
├── controllers/
│   └── productController.js    # API handlers
├── models/
│   ├── Product.js              # Product model
│   ├── Nutrition.js            # Nutrition data
│   ├── Additive.js             # Additive info
│   └── index.js                # Associations
├── routes/
│   └── productRoutes.js        # Route definitions
├── scripts/
│   └── importOFF.js            # OpenFoodFacts import script
├── utils/
│   └── scoreCalculator.js      # Health scoring
├── config/
│   └── database.js             # DB connection
└── app.js                      # Entry point
```

## License

MIT

