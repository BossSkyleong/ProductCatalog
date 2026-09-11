package com.example.neurogineproductcatalog.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neurogineproductcatalog.R;
import com.example.neurogineproductcatalog.data.api.ProductApi;
import com.example.neurogineproductcatalog.data.model.Product;
import com.example.neurogineproductcatalog.data.model.ProductResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayoutManager LayoutManager;
    ProductAdapter adapter;
    List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        LayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(LayoutManager);
        adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);

        fetchProducts();
    }

    private void fetchProducts() {
        progressBar.setVisibility(View.VISIBLE);
        ProductApi.Client.getProductApi().getProducts(0,20).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    productList.addAll(response.body().getProducts());
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(ProductActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                }
                    progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductActivity.this, "Error fetching products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}