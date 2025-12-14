const sequelize = require('../config/database');
const Category = require('./Category');
const Product = require('./Product');
const Nutrition = require('./Nutrition');
const Additive = require('./Additive');
const ProductAdditive = require('./ProductAdditive');

// Associations
Category.hasMany(Product, { foreignKey: 'categoryId' });
Product.belongsTo(Category, { foreignKey: 'categoryId' });

Product.hasOne(Nutrition, { foreignKey: 'productId', onDelete: 'CASCADE' });
Nutrition.belongsTo(Product, { foreignKey: 'productId' });

// Many-to-many: Product <-> Additive through ProductAdditive
Product.belongsToMany(Additive, { through: ProductAdditive, foreignKey: 'productId' });
Additive.belongsToMany(Product, { through: ProductAdditive, foreignKey: 'additiveId' });

module.exports = {
  sequelize,
  Category,
  Product,
  Nutrition,
  Additive,
  ProductAdditive
};

