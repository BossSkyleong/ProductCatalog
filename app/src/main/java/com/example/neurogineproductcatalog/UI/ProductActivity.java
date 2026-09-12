package com.example.neurogineproductcatalog.UI;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neurogineproductcatalog.R;
import com.example.neurogineproductcatalog.data.api.ProductApi;
import com.example.neurogineproductcatalog.data.model.Product;
import com.example.neurogineproductcatalog.data.model.ProductResponse;
import com.example.neurogineproductcatalog.data.repository.ProductRepository;

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

    // Search feature variables
    private EditText searchInput;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private boolean isSearching = false;
    private ProductRepository productRepository;


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

        productRepository = new ProductRepository();
        searchInput = findViewById(R.id.searchInput);


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

        // Search feature (Debounce search input)
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String query = s.toString().trim();

                searchHandler.removeCallbacks(searchRunnable);

                searchRunnable = () -> {
                    if (!query.isEmpty()) {
                        isSearching = true;

                        productList.clear();
                        adapter.notifyDataSetChanged();

                        currentSkip = 0;
                        hasMoreProducts = true;

                        searchProducts(query);

                    } else {
                        isSearching = false;

                        productList.clear();
                        adapter.notifyDataSetChanged();

                        currentSkip = 0;
                        hasMoreProducts = true;

                        fetchProducts();
                    }
                };

                searchHandler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void fetchProducts() {

        if (isLoading || !hasMoreProducts || isSearching) {
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

    private void searchProducts(String query) {

        progressBar.setVisibility(View.VISIBLE);
        hasMoreProducts = false;

        productRepository.searchProducts(query, new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {

                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {

                    productList.clear();
                    List<Product> results = response.body().getProducts();

                    if (results != null) {
                        productList.addAll(results);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ProductActivity.this, "Search failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductActivity.this, "Error searching products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}