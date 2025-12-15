package com.example.productinfoapp.data.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.productinfoapp.data.api.ApiService;
import com.example.productinfoapp.data.api.RetrofitClient;
import com.example.productinfoapp.data.local.AppDatabase;
import com.example.productinfoapp.data.local.ProductDao;
import com.example.productinfoapp.data.local.ProductEntity;
import com.example.productinfoapp.data.model.Product;
import com.example.productinfoapp.data.model.ProductListResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private static final String TAG = "ProductRepository";
    private ProductDao productDao;
    private ApiService apiService;
    private LiveData<List<Product>> allProducts;

    public ProductRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        productDao = db.productDao();
        apiService = RetrofitClient.getApiService();
        
        // Map from Entity to Domain Model
        allProducts = Transformations.map(productDao.getAllProducts(), entities -> {
            List<Product> products = new ArrayList<>();
            if (entities != null) {
                for (ProductEntity entity : entities) {
                    products.add(entity.toProduct());
                }
            }
            Log.d(TAG, "Database returned " + products.size() + " products");
            return products;
        });
    }

    public LiveData<List<Product>> getAllProducts() {
        refreshProducts(); // Trigger network refresh
        return allProducts;
    }
    
    public LiveData<Product> getProductById(String id) {
        // Here we can fetch detail from API to get more info, 
        // updating the local DB mostly for list. 
        // For detail, we can observe DB if list has enough info, 
        // or fetch fresh. I will observe DB.
        
        refreshProductDetail(id);
        
        return Transformations.map(productDao.getProductById(id), entity -> {
            if (entity != null) return entity.toProduct();
            return null;
        });
    }

    public void refreshProducts() {
        Log.d(TAG, "Starting API call to fetch products...");
        // Fetch first page (20 products)
        apiService.getProducts(20, 0).enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(Call<ProductListResponse> call, Response<ProductListResponse> response) {
                Log.d(TAG, "API Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getProducts();
                    Log.d(TAG, "API returned " + products.size() + " products (total: " + response.body().getTotal() + ")");
                    new Thread(() -> {
                        List<ProductEntity> entities = new ArrayList<>();
                        for (Product p : products) {
                            entities.add(new ProductEntity(p));
                        }
                        productDao.insertAll(entities);
                        Log.d(TAG, "Inserted " + entities.size() + " products to database");
                    }).start();
                } else {
                    Log.e(TAG, "API response not successful or body is null");
                }
            }

            @Override
            public void onFailure(Call<ProductListResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching products: " + t.getMessage(), t);
            }
        });
    }
    
    private void refreshProductDetail(String id) {
         apiService.getProductById(id).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                     new Thread(() -> {
                        productDao.insert(new ProductEntity(response.body()));
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                 Log.e("ProductRepository", "Error fetching product detail", t);
            }
        });
    }
}
