const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const Additive = sequelize.define('Additive', {
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true
  },
  code: {
    type: DataTypes.STRING(20),
    allowNull: false,
    unique: true
  },
  name: {
    type: DataTypes.STRING,
    allowNull: false
  },
  riskLevel: {
    type: DataTypes.ENUM('none', 'limited', 'moderate', 'hazardous'),
    defaultValue: 'none'
  },
  description: {
    type: DataTypes.TEXT
  }
}, {
  timestamps: false
});

module.exports = Additive;
