package com.example.neurogineproductcatalog.data.api;

import com.example.neurogineproductcatalog.data.model.ProductResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ProductApi {

    @GET("products")
    Call<ProductResponse> getProducts(
            @Query("skip") int skip,
            @Query("limit") int limit
    );

    @GET("products/{id}")
    Call<ProductResponse> getProductById(
            @Path("id") int id
    );

    @GET("products/search")
    Call<ProductResponse> searchProducts(
            @Query("q") String query
    );

    class Client{
        private static final String BASE_URL = "https://dummyjson.com/";
        private static Retrofit retrofit;

        public static ProductApi getProductApi(){
            if(retrofit == null){
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit.create(ProductApi.class);
        }
    }
}
