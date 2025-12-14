package com.example.productinfoapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Additive implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    @SerializedName("riskLevel")
    private String riskLevel;

    @SerializedName("description")
    private String description;

    public int getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getRiskLevel() { return riskLevel; }
    public String getDescription() { return description; }

    public int getRiskColor() {
        switch (riskLevel != null ? riskLevel : "none") {
            case "hazardous": return 0xFFF44336; // Red
            case "moderate": return 0xFFFF9800; // Orange
            case "limited": return 0xFFFFEB3B; // Yellow
            default: return 0xFF4CAF50; // Green
        }
    }
}
