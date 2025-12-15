package com.example.productinfoapp.ui.productdetail;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.productinfoapp.data.model.Product;
import com.example.productinfoapp.data.repository.ProductRepository;

public class ProductDetailViewModel extends AndroidViewModel {
    private ProductRepository repository;

    public ProductDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new ProductRepository(application);
    }

    public LiveData<Product> getProduct(String id) {
        return repository.getProductById(id);
    }
}
