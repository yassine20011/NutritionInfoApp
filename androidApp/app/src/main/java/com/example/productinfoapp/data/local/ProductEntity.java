package com.example.productinfoapp.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Embedded;
import androidx.room.Ignore;
import com.example.productinfoapp.data.model.Nutrition;
import com.example.productinfoapp.data.model.Product;

@Entity(tableName = "products")
public class ProductEntity {
    @PrimaryKey
    @androidx.annotation.NonNull
    public String id;
    
    public String barcode;
    public String name;
    public String brand;
    public String imageUrl;
    public String ingredients;
    public float score;
    public int calculatedScore;
    public boolean isOrganic;
    public String nutriScore;

    @Embedded
    public Nutrition nutrition;

    public ProductEntity() {}

    @Ignore
    public ProductEntity(Product product) {
        this.id = product.getId();
        this.barcode = product.getBarcode();
        this.name = product.getName();
        this.brand = product.getBrand();
        this.imageUrl = product.getImageUrl();
        this.ingredients = product.getIngredients();
        this.score = product.getScore();
        this.calculatedScore = product.getCalculatedScore();
        this.isOrganic = product.isOrganic();
        this.nutriScore = product.getNutriScore();
        this.nutrition = product.getNutrition();
    }

    public Product toProduct() {
        // Product now uses Gson/reflection for field population
        // We create via reflection pattern since Product has no public constructor
        Product product = new Product();
        product.setId(id);
        product.setBarcode(barcode);
        product.setName(name);
        product.setBrand(brand);
        product.setImageUrl(imageUrl);
        product.setIngredients(ingredients);
        product.setScore(score);
        product.setCalculatedScore(calculatedScore);
        product.setOrganic(isOrganic);
        product.setNutriScore(nutriScore);
        product.setNutrition(nutrition);
        return product;
    }
}

