package com.example.productinfoapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProductListResponse {
    @SerializedName("products")
    private List<Product> products;

    @SerializedName("total")
    private int total;

    @SerializedName("limit")
    private int limit;

    @SerializedName("offset")
    private int offset;

    @SerializedName("hasMore")
    private boolean hasMore;

    public List<Product> getProducts() { return products; }
    public int getTotal() { return total; }
    public int getLimit() { return limit; }
    public int getOffset() { return offset; }
    public boolean hasMore() { return hasMore; }
}
