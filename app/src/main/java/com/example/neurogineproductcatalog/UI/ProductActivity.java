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

    // Pagination variables
    private static final int pageLimit = 20;
    private int currentSkip = 0;
    private boolean isLoading = false;
    private boolean hasMoreProducts = true;


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

        //Load the first 20 products
        fetchProducts();

        //Pagination when user scrolls near the bottom of the list
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = LayoutManager.getChildCount();
                int totalItemCount = LayoutManager.getItemCount();
                int firstVisibleItemPosition = LayoutManager.findFirstVisibleItemPosition();

                if (!isLoading && hasMoreProducts && (visibleItemCount + firstVisibleItemPosition >= totalItemCount)) {
                    fetchProducts();
                }
            }
        });
    }

    private void fetchProducts() {

        if (isLoading || !hasMoreProducts) {
            return;
        }

        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        ProductApi.Client.getProductApi().getProducts(currentSkip, pageLimit).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<Product> newProducts = response.body().getProducts();
                    productList.addAll(newProducts);
                    adapter.notifyDataSetChanged();
                    currentSkip += newProducts.size();

                    if (newProducts.size() < pageLimit) {
                        hasMoreProducts = false;
                    }
                } else {
                    Toast.makeText(ProductActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                }
                isLoading = false;
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                isLoading = false;

                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductActivity.this, "Error fetching products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}