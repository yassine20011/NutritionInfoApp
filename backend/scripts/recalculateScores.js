/**
 * Script to recalculate health scores for all existing products
 * Run with: npm run recalculate
 */
require("dotenv").config();
const { connectDB, Product } = require("../models");
const { calculateHealthScore, getNutriScoreGrade } = require("../utils/scoreCalculator");

async function recalculateScores() {
    try {
        await connectDB();
        console.log("MongoDB connected successfully.");

        const cursor = Product.find().cursor();
        let updated = 0;
        let total = await Product.countDocuments();

        console.log(`Found ${total} products to update.`);

        for await (const product of cursor) {
            const score = calculateHealthScore(product, product.nutrition, product.additives || []);
            const nutriScore = getNutriScoreGrade(score);
            
            await Product.updateOne(
                { _id: product._id },
                { $set: { score, nutriScore } }
            );
            
            updated++;
            if (updated % 1000 === 0) {
                console.log(`Updated ${updated}/${total} products...`);
            }
        }

        console.log(`\nDone! Updated ${updated} products with calculated scores.`);
        process.exit(0);

    } catch (err) {
        console.error("Error:", err.message);
        process.exit(1);
    }
}

recalculateScores();
