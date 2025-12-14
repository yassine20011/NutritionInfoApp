package com.example.productinfoapp.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scan_history")
public class ScanHistoryEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String barcode;
    public String productName;
    public String brandName;
    public String imageUrl;
    public int healthScore;
    public String nutriScoreGrade;
    public boolean isOrganic;
    public boolean isFavorite;
    public long scannedAt;
    public String source; // "local" or "OpenFoodFacts"

    public ScanHistoryEntity() {
        this.scannedAt = System.currentTimeMillis();
        this.isFavorite = false;
    }

    // Static factory from API response
    public static ScanHistoryEntity fromProduct(String barcode, String name, String brand, 
            String imageUrl, int score, String nutriScore, boolean isOrganic, String source) {
        ScanHistoryEntity entity = new ScanHistoryEntity();
        entity.barcode = barcode;
        entity.productName = name;
        entity.brandName = brand;
        entity.imageUrl = imageUrl;
        entity.healthScore = score;
        entity.nutriScoreGrade = nutriScore;
        entity.isOrganic = isOrganic;
        entity.source = source;
        return entity;
    }
}
