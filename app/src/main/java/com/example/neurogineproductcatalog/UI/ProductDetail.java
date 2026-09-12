package com.example.neurogineproductcatalog.UI;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neurogineproductcatalog.R;
import com.example.neurogineproductcatalog.data.model.Product;
import com.example.neurogineproductcatalog.data.model.ProductReview;
import com.example.neurogineproductcatalog.data.repository.ProductRepository;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetail extends AppCompatActivity {

    private ImageView productImage;
    private TextView productTitle;
    private TextView productPrice;
    private TextView productRating;
    private TextView productDesc;
    private LinearLayout reviewsCon;

    private ProductRepository productRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_product_detail);

        productImage = findViewById(R.id.productImage);
        productTitle = findViewById(R.id.productTitle);
        productPrice = findViewById(R.id.productPrice);
        productRating = findViewById(R.id.productRating);
        productDesc = findViewById(R.id.productDesc);
        reviewsCon = findViewById(R.id.reviewsContainer);

        productRepository = new ProductRepository();

        int productId = getIntent().getIntExtra("productId", -1);

        if (productId != -1) {
            fetchProductDetails(productId);
        }
    }

    private void fetchProductDetails(int productId) {
        productRepository.getProductById(productId, new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Product product = response.body();

                    productTitle.setText(product.getTitle());
                    productPrice.setText(String.format("RM%.2f", product.getPrice()));
                    productRating.setText(String.format("Overall Rating: %.1f / 5", product.getRating()));
                    productDesc.setText(product.getDescription());

                    //Display Review
                    reviewsCon.removeAllViews();

                    if(product.getReviews() != null && !product.getReviews().isEmpty()){
                        for(ProductReview review : product.getReviews()){
                            TextView reviewText = new TextView(ProductDetail.this);

                            String reviewContent = "Rating: " + review.getRating() + "/ 5\n"
                                    + review.getReviewerName() + "\n"
                                    + review.getComment()+ "\n"
                                    + review.getDate();

                            reviewText.setText(reviewContent);
                            reviewText.setTextSize(16);
                            reviewText.setPadding(0,12,0,12);
                            reviewsCon.addView(reviewText);
                        }
                    }else{
                        TextView noReviews = new TextView(ProductDetail.this);
                        noReviews.setText("No Reviews available");
                        noReviews.setTextSize(16);

                        reviewsCon.addView(noReviews);
                    }

                    Picasso.get().load(product.getThumbnail()).into(productImage);
                }else{
                    Toast.makeText(ProductDetail.this, "Failed to load Product Details", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(ProductDetail.this, "Error loading Product", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
