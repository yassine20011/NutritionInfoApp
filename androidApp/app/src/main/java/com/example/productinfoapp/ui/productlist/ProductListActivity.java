package com.example.productinfoapp.ui.productlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.productinfoapp.R;
import com.example.productinfoapp.ui.history.HistoryActivity;
import com.example.productinfoapp.ui.scanner.BarcodeScannerActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProductListActivity extends AppCompatActivity {
    private static final String TAG = "ProductListActivity";
    private ProductListViewModel viewModel;
    private ProductAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;
    private FloatingActionButton fabScan, fabHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        Log.d(TAG, "onCreate started");

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyText = findViewById(R.id.emptyText);
        fabScan = findViewById(R.id.fabScan);
        fabHistory = findViewById(R.id.fabHistory);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter();
        recyclerView.setAdapter(adapter);

        // Launch barcode scanner on FAB click
        fabScan.setOnClickListener(v -> {
            Intent intent = new Intent(this, BarcodeScannerActivity.class);
            startActivity(intent);
        });

        // Launch history on FAB click
        fabHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        });

        viewModel = new ViewModelProvider(this).get(ProductListViewModel.class);
        
        progressBar.setVisibility(View.VISIBLE);
        if (emptyText != null) emptyText.setVisibility(View.GONE);
        
        viewModel.getAllProducts().observe(this, products -> {
            progressBar.setVisibility(View.GONE);
            Log.d(TAG, "Received products: " + (products != null ? products.size() : "null"));
            if (products != null && !products.isEmpty()) {
                adapter.setProducts(products);
                recyclerView.setVisibility(View.VISIBLE);
                if (emptyText != null) emptyText.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                if (emptyText != null) emptyText.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from scanner
        if (viewModel != null) {
            viewModel.refreshProducts();
        }
    }
}

