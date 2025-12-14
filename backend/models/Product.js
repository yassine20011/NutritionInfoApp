const mongoose = require('mongoose');

// Embedded schema for nutrition info
const nutritionSchema = new mongoose.Schema({
  calories: { type: Number, default: 0 },
  sugar: { type: Number, default: 0 },
  fat: { type: Number, default: 0 },
  saturatedFat: { type: Number, default: 0 },
  salt: { type: Number, default: 0 },
  protein: { type: Number, default: 0 },
  fiber: { type: Number, default: 0 },
  servingSize: { type: Number, default: 100 },
  servingUnit: { type: String, default: 'g' }
}, { _id: false });

// Embedded schema for additives
const additiveSchema = new mongoose.Schema({
  code: { type: String, required: true },
  name: { type: String },
  riskLevel: { type: String, enum: ['none', 'limited', 'moderate', 'hazardous'], default: 'none' }
}, { _id: false });

// Main product schema
const productSchema = new mongoose.Schema({
  barcode: { type: String, unique: true, sparse: true, index: true },
  name: { type: String, required: true, index: true },
  brand: { type: String, index: true },
  imageUrl: { type: String },
  ingredients: { type: String },
  category: { type: String },
  score: { type: Number, default: 0 },
  isOrganic: { type: Boolean, default: false },
  nutriScore: { type: String, enum: ['A', 'B', 'C', 'D', 'E'], default: null },
  nutrition: nutritionSchema,
  additives: [additiveSchema]
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true }
});

// Text index for search
productSchema.index({ name: 'text', brand: 'text' });

// Virtual for id (to match Sequelize response format)
productSchema.virtual('id').get(function() {
  return this._id.toHexString();
});

const Product = mongoose.model('Product', productSchema);

module.exports = Product;
