const fs = require("fs");
const readline = require("readline");
const os = require("os");
const path = require("path");
const { sequelize, Product, Nutrition, Additive } = require("../models/index.js");

async function importProducts() {
    try {
        // Connect to database first
        await sequelize.authenticate();
        console.log("Database connected successfully.");

        // Sync tables (create if not exist)
        await sequelize.sync();
        console.log("Tables synced.");

        const filePath = path.join(os.homedir(), "Downloads", "openfoodfacts-products.jsonl");
        
        // Check if file exists
        if (!fs.existsSync(filePath)) {
            console.error(`File not found: ${filePath}`);
            process.exit(1);
        }

        console.log(`Reading from: ${filePath}`);
        const fileStream = fs.createReadStream(filePath);

        const rl = readline.createInterface({
            input: fileStream,
            crlfDelay: Infinity,
        });

        let count = 0;
        let errors = 0;

        for await (const line of rl) {
            try {
                const p = JSON.parse(line);

                // Skip empty items
                if (!p.product_name || !p.code) continue;

                // Insert product
                const product = await Product.create({
                    barcode: p.code,
                    name: p.product_name,
                    brand: p.brands,
                    imageUrl: p.image_url,
                    ingredients: p.ingredients_text,
                    nutriScoreGrade: p.nutriscore_grade
                });

                // Insert nutrition details
                if (p.nutriments) {
                    await Nutrition.create({
                        productId: product.id,
                        calories: p.nutriments["energy-kcal_100g"],
                        sugar: p.nutriments["sugars_100g"],
                        fat: p.nutriments["fat_100g"],
                        saturatedFat: p.nutriments["saturated-fat_100g"],
                        salt: p.nutriments["salt_100g"],
                        protein: p.nutriments["proteins_100g"],
                        fiber: p.nutriments["fiber_100g"]
                    });
                }

                // Insert additives
                if (p.additives_tags?.length) {
                    for (const a of p.additives_tags) {
                        await Additive.create({
                            productId: product.id,
                            code: a,
                            name: a.replace("en:", "")
                        });
                    }
                }

                count++;
                if (count % 100 === 0) {
                    console.log(`Imported ${count} products...`);
                }

            } catch (err) {
                errors++;
                if (errors <= 5) {
                    console.error(`Error on line: ${err.message}`);
                }
                continue; // Skip malformed lines
            }
        }

        console.log(`\nImport done. Total: ${count} products, ${errors} errors.`);
        process.exit(0);

    } catch (err) {
        console.error("Fatal error:", err.message);
        process.exit(1);
    }
}

importProducts();
