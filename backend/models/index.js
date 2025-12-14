const { connectDB, mongoose } = require('../config/database');
const Product = require('./Product');

module.exports = {
  connectDB,
  mongoose,
  Product
};
