const { Product } = require('../models');
const { calculateHealthScore, getNutriScoreGrade, getScoreCategory } = require('../utils/scoreCalculator');

// Helper to add score info to product response
function enrichProductWithScore(product) {
  const plain = product.toObject ? product.toObject() : product;
  const score = calculateHealthScore(plain, plain.nutrition, plain.additives || []);
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
    const limit = Math.min(parseInt(req.query.limit) || 20, 100);
    const offset = parseInt(req.query.offset) || 0;

    const [products, count] = await Promise.all([
      Product.find().sort({ _id: -1 }).skip(offset).limit(limit),
      Product.countDocuments()
    ]);

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
    const product = await Product.findById(req.params.id);
    if (!product) return res.status(404).json({ error: 'Product not found' });
    res.json(enrichProductWithScore(product));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

exports.getProductByBarcode = async (req, res) => {
  try {
    const product = await Product.findOne({ barcode: req.params.code });
    
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
    const product = await Product.findById(req.params.id);
    if (!product) return res.status(404).json({ error: 'Product not found' });

    const alternatives = await Product.find({
      _id: { $ne: product._id },
      category: product.category,
      score: { $gt: product.score || 0 }
    })
      .sort({ score: -1 })
      .limit(5);

    res.json(alternatives.map(enrichProductWithScore));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

exports.createProduct = async (req, res) => {
  try {
    const { name, brand, barcode, category, imageUrl, ingredients, isOrganic, nutrition, additives } = req.body;
    
    const product = new Product({
      name, brand, barcode, category, imageUrl, ingredients, isOrganic, nutrition, additives
    });

    // Calculate score before saving
    const score = calculateHealthScore(product, nutrition, additives || []);
    product.score = score;
    product.nutriScore = getNutriScoreGrade(score);

    await product.save();
    res.status(201).json(enrichProductWithScore(product));
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

exports.updateProduct = async (req, res) => {
  try {
    const { nutrition, additives, ...productData } = req.body;
    const product = await Product.findById(req.params.id);
    
    if (!product) return res.status(404).json({ error: 'Product not found' });

    Object.assign(product, productData);
    if (nutrition) product.nutrition = nutrition;
    if (additives !== undefined) product.additives = additives;

    // Recalculate score
    const score = calculateHealthScore(product, product.nutrition, product.additives || []);
    product.score = score;
    product.nutriScore = getNutriScoreGrade(score);

    await product.save();
    res.json(enrichProductWithScore(product));
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

exports.deleteProduct = async (req, res) => {
  try {
    const product = await Product.findByIdAndDelete(req.params.id);
    if (!product) return res.status(404).json({ error: 'Product not found' });
    
    res.json({ message: 'Product deleted' });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

exports.searchProducts = async (req, res) => {
  try {
    const query = req.params.query;
    const products = await Product.find({
      $or: [
        { name: { $regex: query, $options: 'i' } },
        { brand: { $regex: query, $options: 'i' } }
      ]
    }).limit(50);
    
    res.json(products.map(enrichProductWithScore));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};
