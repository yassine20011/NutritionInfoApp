package com.example.productinfoapp.ui.productlist;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.productinfoapp.data.model.Product;
import com.example.productinfoapp.data.repository.ProductRepository;
import java.util.List;

public class ProductListViewModel extends AndroidViewModel {
    private ProductRepository repository;
    private LiveData<List<Product>> allProducts;

    public ProductListViewModel(@NonNull Application application) {
        super(application);
        repository = new ProductRepository(application);
        allProducts = repository.getAllProducts();
    }

    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }

    public void refreshProducts() {
        repository.refreshProducts();
    }
}

