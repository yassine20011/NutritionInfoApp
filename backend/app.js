const express = require('express');
const { sequelize } = require('./models');
const productRoutes = require('./routes/productRoutes');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(express.json());

// Routes
app.use('/products', productRoutes);

// Database Sync and Server Start
// Note: alter: true will automatically update tables to match models
sequelize.sync({ alter: false })
  .then(() => {
    console.log('Database synced successfully.');
    app.listen(PORT, '0.0.0.0', () => {
      console.log(`Server is running on http://0.0.0.0:${PORT}`);
      console.log(`Access from phone: http://192.168.1.11:${PORT}`);
    });
  })
  .catch((err) => {
    console.error('Failed to sync database:', err);
  });
