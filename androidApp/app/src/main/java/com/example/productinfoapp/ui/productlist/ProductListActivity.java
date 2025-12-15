package com.example.productinfoapp.ui.productlist;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.productinfoapp.R;
import com.example.productinfoapp.data.api.ApiService;
import com.example.productinfoapp.data.api.RetrofitClient;
import com.example.productinfoapp.data.model.Product;
import com.example.productinfoapp.ui.history.HistoryActivity;
import com.example.productinfoapp.ui.scanner.BarcodeScannerActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {
    private static final String TAG = "ProductListActivity";
    private ProductAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout emptyStateContainer;
    private EditText searchEditText;
    private FloatingActionButton fabScan, fabHistory;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        Log.d(TAG, "onCreate started");

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);
        searchEditText = findViewById(R.id.searchEditText);
        fabScan = findViewById(R.id.fabScan);
        fabHistory = findViewById(R.id.fabHistory);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter();
        recyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getApiService();

        // Search on Enter key
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                performSearch(searchEditText.getText().toString().trim());
                return true;
            }
            return false;
        });

        // Real-time search with debounce (search after 500ms of no typing)
        searchEditText.addTextChangedListener(new TextWatcher() {
            private final android.os.Handler handler = new android.os.Handler();
            private Runnable searchRunnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.length() >= 2) {
                    searchRunnable = () -> performSearch(query);
                    handler.postDelayed(searchRunnable, 500);
                } else if (query.isEmpty()) {
                    showEmptyState();
                }
            }
        });

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
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            showEmptyState();
            return;
        }

        Log.d(TAG, "Searching for: " + query);
        progressBar.setVisibility(View.VISIBLE);
        emptyStateContainer.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        apiService.searchProducts(query).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    adapter.setProducts(response.body());
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyStateContainer.setVisibility(View.GONE);
                    Log.d(TAG, "Found " + response.body().size() + " products");
                } else {
                    showNoResults();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Search failed", t);
                showNoResults();
            }
        });
    }

    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        emptyStateContainer.setVisibility(View.VISIBLE);
        TextView emptyText = findViewById(R.id.emptyText);
        if (emptyText != null) {
            emptyText.setText("Scan a product barcode\nor search by name");
        }
    }

    private void showNoResults() {
        recyclerView.setVisibility(View.GONE);
        emptyStateContainer.setVisibility(View.VISIBLE);
        TextView emptyText = findViewById(R.id.emptyText);
        if (emptyText != null) {
            emptyText.setText("No products found.\nTry a different search term.");
        }
    }
}
