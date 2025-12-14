const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const ProductAdditive = sequelize.define('ProductAdditive', {
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true
  },
  productId: {
    type: DataTypes.INTEGER,
    allowNull: false
  },
  additiveId: {
    type: DataTypes.INTEGER,
    allowNull: false
  }
}, {
  timestamps: false
});

module.exports = ProductAdditive;
