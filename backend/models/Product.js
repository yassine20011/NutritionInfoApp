const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const Product = sequelize.define('Product', {
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true
  },
  barcode: {
    type: DataTypes.STRING(50),
    unique: true
  },
  name: {
    type: DataTypes.STRING,
    allowNull: false
  },
  brand: {
    type: DataTypes.STRING
  },
  imageUrl: {
    type: DataTypes.STRING
  },
  ingredients: {
    type: DataTypes.TEXT
  },
  score: {
    type: DataTypes.FLOAT
  },
  isOrganic: {
    type: DataTypes.BOOLEAN,
    defaultValue: false
  },
  nutriScore: {
    type: DataTypes.ENUM('A', 'B', 'C', 'D', 'E'),
    allowNull: true
  }
});

module.exports = Product;

