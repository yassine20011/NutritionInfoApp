package com.example.productinfoapp.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.productinfoapp.R;
import com.example.productinfoapp.data.local.AppDatabase;
import com.example.productinfoapp.data.local.ScanHistoryDao;
import com.example.productinfoapp.data.local.ScanHistoryEntity;
import com.example.productinfoapp.ui.scanner.BarcodeScannerActivity;
import com.google.android.material.tabs.TabLayout;
import java.util.concurrent.Executors;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView emptyText;
    private TabLayout tabLayout;
    private HistoryAdapter adapter;
    private ScanHistoryDao historyDao;
    private boolean showingFavorites = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.historyRecyclerView);
        emptyText = findViewById(R.id.emptyText);
        tabLayout = findViewById(R.id.tabLayout);

        historyDao = AppDatabase.getInstance(this).scanHistoryDao();

        setupRecyclerView();
        setupTabs();
        observeHistory();
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            // Open scanner with barcode to re-fetch product
            Intent intent = new Intent(this, BarcodeScannerActivity.class);
            intent.putExtra("barcode", item.barcode);
            startActivity(intent);
        });

        adapter.setOnFavoriteToggleListener((item, isFavorite) -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                historyDao.setFavorite(item.id, isFavorite);
            });
        });
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showingFavorites = tab.getPosition() == 1;
                observeHistory();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void observeHistory() {
        if (showingFavorites) {
            emptyText.setText("No favorites yet.\nTap the star to add favorites!");
            historyDao.getFavorites().observe(this, this::updateList);
        } else {
            emptyText.setText("No scan history yet.\nScan a product to get started!");
            historyDao.getAllHistory().observe(this, this::updateList);
        }
    }

    private void updateList(java.util.List<ScanHistoryEntity> items) {
        if (items == null || items.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
            adapter.setItems(items);
        }
    }
}
