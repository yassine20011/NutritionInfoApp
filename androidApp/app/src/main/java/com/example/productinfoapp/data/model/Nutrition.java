package com.example.productinfoapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Nutrition implements Serializable {
    @SerializedName("calories")
    private double calories;

    @SerializedName("sugar")
    private double sugar;

    @SerializedName("fat")
    private double fat;

    @SerializedName("saturatedFat")
    private double saturatedFat;

    @SerializedName("salt")
    private double salt;

    @SerializedName("protein")
    private double protein;

    @SerializedName("fiber")
    private double fiber;

    @SerializedName("servingSize")
    private double servingSize;

    @SerializedName("servingUnit")
    private String servingUnit;

    public Nutrition() {}

    // Getters
    public double getCalories() { return calories; }
    public double getSugar() { return sugar; }
    public double getFat() { return fat; }
    public double getSaturatedFat() { return saturatedFat; }
    public double getSalt() { return salt; }
    public double getProtein() { return protein; }
    public double getFiber() { return fiber; }
    public double getServingSize() { return servingSize; }
    public String getServingUnit() { return servingUnit; }

    // Setters (required by Room @Embedded)
    public void setCalories(double calories) { this.calories = calories; }
    public void setSugar(double sugar) { this.sugar = sugar; }
    public void setFat(double fat) { this.fat = fat; }
    public void setSaturatedFat(double saturatedFat) { this.saturatedFat = saturatedFat; }
    public void setSalt(double salt) { this.salt = salt; }
    public void setProtein(double protein) { this.protein = protein; }
    public void setFiber(double fiber) { this.fiber = fiber; }
    public void setServingSize(double servingSize) { this.servingSize = servingSize; }
    public void setServingUnit(String servingUnit) { this.servingUnit = servingUnit; }
}


