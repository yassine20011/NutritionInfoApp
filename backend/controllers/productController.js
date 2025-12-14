const { Product, Category, Nutrition, Additive } = require('../models');
const { calculateHealthScore, getNutriScoreGrade, getScoreCategory } = require('../utils/scoreCalculator');
const { Op } = require('sequelize');

const includeAll = [Category, Nutrition, Additive];

// Helper to add score info to product response
function enrichProductWithScore(product) {
  const plain = product.toJSON();
  const score = calculateHealthScore(plain, plain.Nutrition, plain.Additives || []);
  const category = getScoreCategory(score);
  return {
    ...plain,
    calculatedScore: score,
    scoreCategory: category,
    nutriScoreGrade: getNutriScoreGrade(score)
  };
}

exports.getAllProducts = async (req, res) => {
  try {
    // Pagination: ?limit=20&offset=0
    const limit = Math.min(parseInt(req.query.limit) || 20, 100); // Max 100
    const offset = parseInt(req.query.offset) || 0;

    const { count, rows: products } = await Product.findAndCountAll({
      include: includeAll,
      limit,
      offset,
      order: [['id', 'DESC']] // Newest first
    });

    const enriched = products.map(enrichProductWithScore);
    res.json({
      products: enriched,
      total: count,
      limit,
      offset,
      hasMore: offset + products.length < count
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

exports.getProductById = async (req, res) => {
  try {
    const product = await Product.findByPk(req.params.id, { include: includeAll });
    if (!product) return res.status(404).json({ error: 'Product not found' });
    res.json(enrichProductWithScore(product));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

exports.getProductByBarcode = async (req, res) => {
  try {
    const product = await Product.findOne({
      where: { barcode: req.params.code },
      include: includeAll
    });
    
    if (product) {
      return res.json(enrichProductWithScore(product));
    }

    res.status(404).json({ error: 'Product not found' });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

exports.getAlternatives = async (req, res) => {
  try {
    const product = await Product.findByPk(req.params.id, { include: [Category] });
    if (!product) return res.status(404).json({ error: 'Product not found' });

    // Find similar products in same category with higher score
    const alternatives = await Product.findAll({
      where: {
        id: { [Op.ne]: product.id },
        categoryId: product.categoryId,
        score: { [Op.gt]: product.score || 0 }
      },
      include: includeAll,
      order: [['score', 'DESC']],
      limit: 5
    });

    res.json(alternatives.map(enrichProductWithScore));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

exports.createProduct = async (req, res) => {
  try {
    const { name, brand, barcode, categoryId, imageUrl, ingredients, isOrganic, nutrition, additiveIds } = req.body;
    
    const product = await Product.create({
      name, brand, barcode, categoryId, imageUrl, ingredients, isOrganic
    });

    if (nutrition) {
      await product.createNutrition(nutrition);
    }

    if (additiveIds && additiveIds.length > 0) {
      await product.setAdditives(additiveIds);
    }
    
    // Recalculate score
    const fullProduct = await Product.findByPk(product.id, { include: includeAll });
    const score = calculateHealthScore(fullProduct.toJSON(), fullProduct.Nutrition?.toJSON(), fullProduct.Additives || []);
    await fullProduct.update({ score, nutriScore: getNutriScoreGrade(score) });

    const final = await Product.findByPk(product.id, { include: includeAll });
    res.status(201).json(enrichProductWithScore(final));
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

exports.updateProduct = async (req, res) => {
  try {
    const { nutrition, additiveIds, ...productData } = req.body;
    const product = await Product.findByPk(req.params.id);
    
    if (!product) return res.status(404).json({ error: 'Product not found' });

    await product.update(productData);

    if (nutrition) {
      const existingNutrition = await product.getNutrition();
      if (existingNutrition) {
        await existingNutrition.update(nutrition);
      } else {
        await product.createNutrition(nutrition);
      }
    }

    if (additiveIds !== undefined) {
      await product.setAdditives(additiveIds);
    }

    // Recalculate score
    const fullProduct = await Product.findByPk(product.id, { include: includeAll });
    const score = calculateHealthScore(fullProduct.toJSON(), fullProduct.Nutrition?.toJSON(), fullProduct.Additives || []);
    await fullProduct.update({ score, nutriScore: getNutriScoreGrade(score) });

    const updatedProduct = await Product.findByPk(product.id, { include: includeAll });
    res.json(enrichProductWithScore(updatedProduct));
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

exports.deleteProduct = async (req, res) => {
  try {
    const product = await Product.findByPk(req.params.id);
    if (!product) return res.status(404).json({ error: 'Product not found' });
    
    await product.destroy();
    res.json({ message: 'Product deleted' });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

exports.searchProducts = async (req, res) => {
  try {
    const query = req.params.query;
    const products = await Product.findAll({
      where: {
        [Op.or]: [
          { name: { [Op.like]: `%${query}%` } },
          { brand: { [Op.like]: `%${query}%` } }
        ]
      },
      include: includeAll,
      limit: 50
    });
    res.json(products.map(enrichProductWithScore));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};
