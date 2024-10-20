package com.mastercoding.thriftly.Models;

import com.google.firebase.Timestamp;

public class Product {
    private String category;
    private String description;
    private String imageUrl;
    private String name;
    private String price;
    private String status;
    private Timestamp timestamp;  // Sử dụng Firebase Timestamp
    private String userId;

    // Constructor mặc định cần thiết cho Firestore
    public Product() {
    }

    public Product(String category, String description, String imageUrl, String name, String price, String status, Timestamp timestamp, String userId) {
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.status = status;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    // Getters và setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}