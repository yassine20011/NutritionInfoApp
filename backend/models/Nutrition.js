const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const Nutrition = sequelize.define('Nutrition', {
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true
  },
  calories: {
    type: DataTypes.FLOAT,
    defaultValue: 0
  },
  sugar: {
    type: DataTypes.FLOAT,
    defaultValue: 0
  },
  fat: {
    type: DataTypes.FLOAT,
    defaultValue: 0
  },
  saturatedFat: {
    type: DataTypes.FLOAT,
    defaultValue: 0
  },
  salt: {
    type: DataTypes.FLOAT,
    defaultValue: 0
  },
  protein: {
    type: DataTypes.FLOAT,
    defaultValue: 0
  },
  fiber: {
    type: DataTypes.FLOAT,
    defaultValue: 0
  },
  servingSize: {
    type: DataTypes.FLOAT,
    defaultValue: 100
  },
  servingUnit: {
    type: DataTypes.STRING(20),
    defaultValue: 'g'
  }
}, {
  timestamps: false
});

module.exports = Nutrition;

