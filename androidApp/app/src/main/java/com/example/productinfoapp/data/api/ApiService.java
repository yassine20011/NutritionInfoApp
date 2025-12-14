package com.example.productinfoapp.data.api;

import com.example.productinfoapp.data.model.Product;
import com.example.productinfoapp.data.model.ProductListResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("products")
    Call<ProductListResponse> getProducts(@Query("limit") int limit, @Query("offset") int offset);

    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") int id);

    @GET("products/barcode/{code}")
    Call<Product> getProductByBarcode(@Path("code") String barcode);

    @GET("products/{id}/alternatives")
    Call<List<Product>> getAlternatives(@Path("id") int id);

    @GET("products/search/{query}")
    Call<List<Product>> searchProducts(@Path("query") String query);
}
