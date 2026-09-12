package com.example.neurogineproductcatalog.UI;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    //Pull-to-Refresh variables
    private SwipeRefreshLayout swipeRefreshLayout;

    //Retry Button variables
    private Button retryButton;
    private LinearLayout errorLayout;


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

        //search feature
        productRepository = new ProductRepository();
        searchInput = findViewById(R.id.searchInput);

        //Pull-to-Refresh
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        //Retry button when internet fail`
        retryButton = findViewById(R.id.retryButton);
        errorLayout = findViewById(R.id.errorLayout);

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

        //pull-to-refresh onclick
        swipeRefreshLayout.setOnRefreshListener(() -> {
            productList.clear();
            adapter.notifyDataSetChanged();

            currentSkip = 0;
            hasMoreProducts = true;
            isLoading = false;

            errorLayout.setVisibility(View.GONE);
            fetchProducts();
        });

        //retry button onclick
        retryButton.setOnClickListener(v -> {
            errorLayout.setVisibility(View.GONE);

            currentSkip = 0;
            hasMoreProducts = true;
            isLoading = false;

            fetchProducts();
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

                    errorLayout.setVisibility(View.GONE);

                    List<Product> newProducts = response.body().getProducts();
                    productList.addAll(newProducts);
                    adapter.notifyDataSetChanged();
                    currentSkip += newProducts.size();

                    if (newProducts.size() < pageLimit) {
                        hasMoreProducts = false;
                    }
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                }
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                errorLayout.setVisibility(View.VISIBLE);
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