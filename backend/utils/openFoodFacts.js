/**
 * Open Food Facts API Integration
 * 
 * Provides real product data from the Open Food Facts database
 * when products are not found in the local backend.
 */

const axios = require('axios');
const { calculateHealthScore, getNutriScoreGrade, getScoreCategory } = require('./scoreCalculator');

const OFF_API_BASE = 'https://world.openfoodfacts.org/api/v2';

/**
 * Fetch product from Open Food Facts by barcode
 * @param {string} barcode - Product barcode (EAN-13, UPC-A, etc.)
 * @returns {Promise<Object|null>} Formatted product or null if not found
 */
async function fetchFromOpenFoodFacts(barcode) {
  try {
    const response = await axios.get(`${OFF_API_BASE}/product/${barcode}.json`, {
      timeout: 10000,
      headers: {
        'User-Agent': 'ProductInfoApp/1.0 - Android - learning project'
      }
    });

    if (response.data.status !== 1 || !response.data.product) {
      return null;
    }

    const offProduct = response.data.product;
    return formatOpenFoodFactsProduct(offProduct);
  } catch (error) {
    console.error('Open Food Facts API error:', error.message);
    return null;
  }
}

/**
 * Format Open Food Facts product data to our schema
 */
function formatOpenFoodFactsProduct(offProduct) {
  const nutrition = {
    calories: offProduct.nutriments?.['energy-kcal_100g'] || 0,
    sugar: offProduct.nutriments?.sugars_100g || 0,
    fat: offProduct.nutriments?.fat_100g || 0,
    saturatedFat: offProduct.nutriments?.['saturated-fat_100g'] || 0,
    salt: offProduct.nutriments?.salt_100g || 0,
    protein: offProduct.nutriments?.proteins_100g || 0,
    fiber: offProduct.nutriments?.fiber_100g || 0,
    servingSize: 100,
    servingUnit: 'g'
  };

  // Parse additives from ingredients
  const additives = parseAdditives(offProduct.additives_tags || []);

  // Check organic label
  const labels = offProduct.labels_tags || [];
  const isOrganic = labels.some(l => 
    l.toLowerCase().includes('organic') || 
    l.toLowerCase().includes('bio')
  );

  // Calculate our health score
  const product = { isOrganic };
  const healthScore = calculateHealthScore(product, nutrition, additives);

  return {
    barcode: offProduct.code,
    name: offProduct.product_name || offProduct.product_name_en || 'Unknown Product',
    brand: offProduct.brands || 'Unknown Brand',
    imageUrl: offProduct.image_front_url || offProduct.image_url || null,
    ingredients: offProduct.ingredients_text || offProduct.ingredients_text_en || null,
    isOrganic,
    nutriScore: offProduct.nutriscore_grade?.toUpperCase() || getNutriScoreGrade(healthScore),
    score: healthScore,
    calculatedScore: healthScore,
    scoreCategory: getScoreCategory(healthScore),
    nutriScoreGrade: offProduct.nutriscore_grade?.toUpperCase() || getNutriScoreGrade(healthScore),
    Nutrition: nutrition,
    Additives: additives,
    source: 'OpenFoodFacts'
  };
}

/**
 * Parse additive tags from Open Food Facts format
 */
function parseAdditives(tags) {
  const riskLevels = {
    // Common concerning additives
    'e102': 'moderate', 'e104': 'moderate', 'e110': 'hazardous',
    'e120': 'limited', 'e122': 'moderate', 'e124': 'hazardous',
    'e129': 'hazardous', 'e131': 'moderate', 'e132': 'moderate',
    'e133': 'limited', 'e142': 'moderate', 'e150a': 'limited',
    'e150c': 'moderate', 'e150d': 'hazardous', 'e151': 'moderate',
    'e154': 'hazardous', 'e160a': 'none', 'e160b': 'limited',
    'e171': 'hazardous', 'e200': 'none', 'e202': 'none',
    'e211': 'hazardous', 'e220': 'hazardous', 'e250': 'hazardous',
    'e251': 'hazardous', 'e260': 'none', 'e270': 'none',
    'e290': 'none', 'e300': 'none', 'e306': 'none',
    'e307': 'none', 'e330': 'none', 'e331': 'none',
    'e338': 'moderate', 'e339': 'moderate', 'e340': 'moderate',
    'e341': 'limited', 'e407': 'moderate', 'e410': 'none',
    'e412': 'none', 'e414': 'none', 'e415': 'none',
    'e420': 'limited', 'e422': 'none', 'e440': 'none',
    'e450': 'moderate', 'e451': 'moderate', 'e452': 'moderate',
    'e460': 'none', 'e466': 'none', 'e471': 'none',
    'e472': 'none', 'e500': 'none', 'e503': 'none',
    'e509': 'none', 'e621': 'moderate', 'e627': 'limited',
    'e631': 'limited', 'e950': 'moderate', 'e951': 'hazardous',
    'e952': 'hazardous', 'e954': 'moderate', 'e955': 'moderate'
  };

  return tags.map(tag => {
    // Format: en:e100 -> E100
    const code = tag.replace(/^[a-z-]+:/, '').toUpperCase();
    const codeLower = code.toLowerCase();
    
    return {
      code: code,
      name: getAdditiveName(code),
      riskLevel: riskLevels[codeLower] || 'limited',
      description: null
    };
  }).filter(a => a.code.match(/^E\d+/)); // Only E-number additives
}

/**
 * Get human-readable additive name (simplified)
 */
function getAdditiveName(code) {
  const names = {
    'E100': 'Curcumin', 'E101': 'Riboflavin', 'E102': 'Tartrazine',
    'E104': 'Quinoline Yellow', 'E110': 'Sunset Yellow',
    'E120': 'Carmine', 'E122': 'Azorubine', 'E124': 'Ponceau 4R',
    'E129': 'Allura Red', 'E131': 'Patent Blue V',
    'E150A': 'Caramel', 'E150D': 'Sulfite Ammonia Caramel',
    'E160A': 'Beta-Carotene', 'E171': 'Titanium Dioxide',
    'E200': 'Sorbic Acid', 'E202': 'Potassium Sorbate',
    'E211': 'Sodium Benzoate', 'E220': 'Sulfur Dioxide',
    'E250': 'Sodium Nitrite', 'E251': 'Sodium Nitrate',
    'E260': 'Acetic Acid', 'E270': 'Lactic Acid',
    'E290': 'Carbon Dioxide', 'E300': 'Vitamin C',
    'E306': 'Vitamin E', 'E330': 'Citric Acid',
    'E338': 'Phosphoric Acid', 'E407': 'Carrageenan',
    'E412': 'Guar Gum', 'E415': 'Xanthan Gum',
    'E420': 'Sorbitol', 'E440': 'Pectin',
    'E450': 'Diphosphates', 'E471': 'Mono/Diglycerides',
    'E500': 'Sodium Carbonates', 'E621': 'MSG',
    'E951': 'Aspartame', 'E952': 'Cyclamate', 'E955': 'Sucralose'
  };
  return names[code.toUpperCase()] || code;
}

module.exports = {
  fetchFromOpenFoodFacts,
  formatOpenFoodFactsProduct,
  parseAdditives
};
