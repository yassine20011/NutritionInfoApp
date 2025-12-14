package com.example.productinfoapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("barcode")
    private String barcode;

    @SerializedName("name")
    private String name;

    @SerializedName("brand")
    private String brand;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("ingredients")
    private String ingredients;

    @SerializedName("score")
    private float score;

    @SerializedName("calculatedScore")
    private int calculatedScore;

    @SerializedName("isOrganic")
    private boolean isOrganic;

    @SerializedName("nutriScore")
    private String nutriScore;

    @SerializedName("nutriScoreGrade")
    private String nutriScoreGrade;

    @SerializedName("Nutrition")
    private Nutrition nutrition;

    @SerializedName("scoreCategory")
    private ScoreCategory scoreCategory;

    @SerializedName("Additives")
    private List<Additive> additives;

    // Getters
    public int getId() { return id; }
    public String getBarcode() { return barcode; }
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public String getImageUrl() { return imageUrl; }
    public String getIngredients() { return ingredients; }
    public float getScore() { return score; }
    public int getCalculatedScore() { return calculatedScore; }
    public boolean isOrganic() { return isOrganic; }
    public String getNutriScore() { return nutriScore; }
    public String getNutriScoreGrade() { return nutriScoreGrade; }
    public Nutrition getNutrition() { return nutrition; }
    public ScoreCategory getScoreCategory() { return scoreCategory; }
    public List<Additive> getAdditives() { return additives; }

    // Default constructor for Gson and Room
    public Product() {}

    // Setters (for Room entity conversion)
    public void setId(int id) { this.id = id; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public void setName(String name) { this.name = name; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
    public void setScore(float score) { this.score = score; }
    public void setCalculatedScore(int calculatedScore) { this.calculatedScore = calculatedScore; }
    public void setOrganic(boolean organic) { this.isOrganic = organic; }
    public void setNutriScore(String nutriScore) { this.nutriScore = nutriScore; }
    public void setNutrition(Nutrition nutrition) { this.nutrition = nutrition; }

    // Inner class for score category
    public static class ScoreCategory implements Serializable {
        @SerializedName("label")
        private String label;
        
        @SerializedName("color")
        private String color;

        public String getLabel() { return label; }
        public String getColor() { return color; }
    }
}

