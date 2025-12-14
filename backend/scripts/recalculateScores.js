/**
 * Script to recalculate health scores for all existing products
 * Run with: node scripts/recalculateScores.js
 */
const { sequelize, Product, Nutrition, Additive } = require("../models/index.js");
const { calculateHealthScore, getNutriScoreGrade } = require("../utils/scoreCalculator.js");

async function recalculateScores() {
    try {
        await sequelize.authenticate();
        console.log("Database connected successfully.");

        const products = await Product.findAll({
            include: [Nutrition, Additive]
        });

        console.log(`Found ${products.length} products to update.`);

        let updated = 0;
        for (const product of products) {
            const plain = product.toJSON();
            const score = calculateHealthScore(plain, plain.Nutrition, plain.Additives || []);
            const nutriScore = getNutriScoreGrade(score);
            
            await product.update({ score, nutriScore });
            updated++;

            if (updated % 100 === 0) {
                console.log(`Updated ${updated}/${products.length} products...`);
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
