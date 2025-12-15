package com.example.productinfoapp.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM products")
    LiveData<List<ProductEntity>> getAllProducts();

    @Query("SELECT * FROM products WHERE id = :id")
    LiveData<ProductEntity> getProductById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ProductEntity> products);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProductEntity product);
}
