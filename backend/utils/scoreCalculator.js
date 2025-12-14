/**
 * NutriScore-inspired Health Score Calculator
 * 
 * Score composition (0-100):
 * - Nutritional Quality: 60% 
 * - Additives: 30%
 * - Organic Bonus: 10%
 */

// Risk level penalties for additives
const ADDITIVE_PENALTIES = {
  none: 0,
  limited: 5,
  moderate: 15,
  hazardous: 30
};

// Thresholds for negative points (per 100g)
const NEGATIVE_THRESHOLDS = {
  calories: [335, 670, 1005, 1340, 1675, 2010, 2345, 2680, 3015, 3350],
  sugar: [4.5, 9, 13.5, 18, 22.5, 27, 31, 36, 40, 45],
  saturatedFat: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
  salt: [0.09, 0.18, 0.27, 0.36, 0.45, 0.54, 0.63, 0.72, 0.81, 0.9]
};

// Thresholds for positive points (per 100g)
const POSITIVE_THRESHOLDS = {
  protein: [1.6, 3.2, 4.8, 6.4, 8],
  fiber: [0.9, 1.9, 2.8, 3.7, 4.7]
};

/**
 * Calculate points based on thresholds
 */
function getPoints(value, thresholds) {
  for (let i = 0; i < thresholds.length; i++) {
    if (value <= thresholds[i]) return i;
  }
  return thresholds.length;
}

/**
 * Calculate nutritional quality score (0-60 points)
 */
function calculateNutritionalScore(nutrition) {
  if (!nutrition) return 30; // Default to middle score if no data

  const negativePoints = 
    getPoints(nutrition.calories || 0, NEGATIVE_THRESHOLDS.calories) +
    getPoints(nutrition.sugar || 0, NEGATIVE_THRESHOLDS.sugar) +
    getPoints(nutrition.saturatedFat || 0, NEGATIVE_THRESHOLDS.saturatedFat) +
    getPoints(nutrition.salt || 0, NEGATIVE_THRESHOLDS.salt);

  const positivePoints = 
    getPoints(nutrition.protein || 0, POSITIVE_THRESHOLDS.protein) +
    getPoints(nutrition.fiber || 0, POSITIVE_THRESHOLDS.fiber);

  // Max negative = 40, max positive = 13
  // Convert to 0-60 scale: higher is better
  const rawScore = positivePoints - negativePoints;
  const normalized = ((rawScore + 40) / 53) * 60;
  
  return Math.max(0, Math.min(60, normalized));
}

/**
 * Calculate additive penalty (0-30 points deducted)
 */
function calculateAdditivePenalty(additives) {
  if (!additives || additives.length === 0) return 0;

  let totalPenalty = 0;
  for (const additive of additives) {
    totalPenalty += ADDITIVE_PENALTIES[additive.riskLevel] || 0;
  }

  return Math.min(30, totalPenalty);
}

/**
 * Calculate final health score (0-100)
 */
function calculateHealthScore(product, nutrition, additives) {
  const nutritionalScore = calculateNutritionalScore(nutrition);
  const additivePenalty = calculateAdditivePenalty(additives);
  const organicBonus = product.isOrganic ? 10 : 0;

  const finalScore = nutritionalScore - additivePenalty + organicBonus;
  return Math.max(0, Math.min(100, Math.round(finalScore)));
}

/**
 * Determine NutriScore grade based on score
 */
function getNutriScoreGrade(score) {
  if (score >= 75) return 'A';
  if (score >= 50) return 'B';
  if (score >= 25) return 'C';
  if (score >= 10) return 'D';
  return 'E';
}

/**
 * Get score category for display
 */
function getScoreCategory(score) {
  if (score >= 75) return { label: 'Excellent', color: '#4CAF50' };
  if (score >= 50) return { label: 'Good', color: '#8BC34A' };
  if (score >= 25) return { label: 'Poor', color: '#FF9800' };
  return { label: 'Bad', color: '#F44336' };
}

module.exports = {
  calculateHealthScore,
  calculateNutritionalScore,
  calculateAdditivePenalty,
  getNutriScoreGrade,
  getScoreCategory
};
