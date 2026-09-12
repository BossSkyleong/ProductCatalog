package com.example.neurogineproductcatalog.data.repository;

import com.example.neurogineproductcatalog.data.api.ProductApi;
import com.example.neurogineproductcatalog.data.model.Product;
import com.example.neurogineproductcatalog.data.model.ProductResponse;

import retrofit2.Call;
import retrofit2.Callback;

public class ProductRepository {

    private final ProductApi productApi;

    public ProductRepository() {
        productApi = ProductApi.Client.getProductApi();
    }

    public void getProducts(int limit, int skip, Callback<ProductResponse> callback){
        Call<ProductResponse> call = productApi.getProducts(limit, skip);
        call.enqueue(callback);
    }

    public void getProductById(int id, Callback<Product> callback){
        Call<Product> call = productApi.getProductById(id);
        call.enqueue(callback);
    }

    public void searchProducts(String query, Callback<ProductResponse> callback){
        Call<ProductResponse> call = productApi.searchProducts(query);
        call.enqueue(callback);
    }
}
