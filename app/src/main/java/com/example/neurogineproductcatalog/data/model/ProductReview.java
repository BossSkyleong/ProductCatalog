package com.example.neurogineproductcatalog.data.model;

public class ProductReview {

    private double rating;
    private String comment;
    private String date;
    private String reviewerName;
    private String reviewrEmail;

    public double getRating(){
        return rating;
    }

    public String getComment(){
        return comment;
    }

    public String getDate(){
        return date;
    }

    public String getReviewerName(){
        return reviewerName;
    }

}
