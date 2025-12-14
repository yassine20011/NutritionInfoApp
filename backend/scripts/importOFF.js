const fs = require("fs");
const readline = require("readline");
const os = require("os");
const path = require("path");
require("dotenv").config();
const { connectDB, Product } = require("../models");
const { calculateHealthScore, getNutriScoreGrade } = require("../utils/scoreCalculator");

const BATCH_SIZE = 1000;

async function importProducts() {
    try {
        await connectDB();
        console.log("MongoDB connected successfully.");

        const filePath = path.join(os.homedir(), "Downloads", "openfoodfacts-products.jsonl");
        
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

        let batch = [];
        let count = 0;
        let errors = 0;

        for await (const line of rl) {
            try {
                const p = JSON.parse(line);

                // Skip empty items
                if (!p.product_name || !p.code) continue;

                // Build nutrition object
                const nutrition = p.nutriments ? {
                    calories: p.nutriments["energy-kcal_100g"] || 0,
                    sugar: p.nutriments["sugars_100g"] || 0,
                    fat: p.nutriments["fat_100g"] || 0,
                    saturatedFat: p.nutriments["saturated-fat_100g"] || 0,
                    salt: p.nutriments["salt_100g"] || 0,
                    protein: p.nutriments["proteins_100g"] || 0,
                    fiber: p.nutriments["fiber_100g"] || 0
                } : null;

                // Build additives array
                const additives = (p.additives_tags || []).map(a => ({
                    code: a,
                    name: a.replace("en:", ""),
                    riskLevel: "none"
                }));

                // Calculate health score
                const productData = {
                    barcode: p.code,
                    name: p.product_name,
                    brand: p.brands,
                    imageUrl: p.image_url,
                    ingredients: p.ingredients_text,
                    category: p.categories_tags?.[0]?.replace("en:", "") || null,
                    isOrganic: p.labels_tags?.includes("en:organic") || false,
                    nutrition,
                    additives
                };

                const score = calculateHealthScore(productData, nutrition, additives);
                productData.score = score;
                productData.nutriScore = getNutriScoreGrade(score);

                batch.push(productData);

                // Bulk insert when batch is full
                if (batch.length >= BATCH_SIZE) {
                    await Product.insertMany(batch, { ordered: false }).catch(err => {
                        // Ignore duplicate key errors
                        if (err.code !== 11000) throw err;
                    });
                    count += batch.length;
                    console.log(`Imported ${count} products...`);
                    batch = [];
                }

            } catch (err) {
                errors++;
                if (errors <= 5) {
                    console.error(`Error on line: ${err.message}`);
                }
                continue;
            }
        }

        // Insert remaining batch
        if (batch.length > 0) {
            await Product.insertMany(batch, { ordered: false }).catch(err => {
                if (err.code !== 11000) throw err;
            });
            count += batch.length;
        }

        console.log(`\nImport done. Total: ${count} products, ${errors} errors.`);
        process.exit(0);

    } catch (err) {
        console.error("Fatal error:", err.message);
        process.exit(1);
    }
}

importProducts();
