const express = require('express');
const router = express.Router();
const productController = require('../controllers/productController');

router.get('/', productController.getAllProducts);
router.get('/search/:query', productController.searchProducts);
router.get('/barcode/:code', productController.getProductByBarcode);
router.get('/:id', productController.getProductById);
router.get('/:id/alternatives', productController.getAlternatives);
router.post('/', productController.createProduct);
router.put('/:id', productController.updateProduct);
router.delete('/:id', productController.deleteProduct);

module.exports = router;


