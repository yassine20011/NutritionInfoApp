package com.example.productinfoapp.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ScanHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ScanHistoryEntity history);

    @Query("SELECT * FROM scan_history ORDER BY scannedAt DESC")
    LiveData<List<ScanHistoryEntity>> getAllHistory();

    @Query("SELECT * FROM scan_history WHERE isFavorite = 1 ORDER BY scannedAt DESC")
    LiveData<List<ScanHistoryEntity>> getFavorites();

    @Query("SELECT * FROM scan_history WHERE barcode = :barcode LIMIT 1")
    ScanHistoryEntity getByBarcode(String barcode);

    @Update
    void update(ScanHistoryEntity history);

    @Query("UPDATE scan_history SET isFavorite = :isFavorite WHERE id = :id")
    void setFavorite(int id, boolean isFavorite);

    @Query("DELETE FROM scan_history WHERE id = :id")
    void delete(int id);

    @Query("DELETE FROM scan_history WHERE isFavorite = 0")
    void clearNonFavorites();
}
