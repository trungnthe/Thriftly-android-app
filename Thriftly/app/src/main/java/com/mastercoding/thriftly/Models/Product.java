package com.mastercoding.thriftly.Models;

import com.google.firebase.Timestamp;


public class Product {
    private String id;         // ID của sản phẩm (document ID)
    private String category;
    private String description;
    private String imageUrl;
    private String name;
    private String price;
    private String userId;
    private Timestamp timestamp;
    private String status;
    private String categoryId;

    // Constructor không tham số
    public Product() {
        // Cần có constructor rỗng cho Firestore
    }

    // Constructor đầy đủ tham số
    public Product(String id, String category, String description, String imageUrl, String name, String price, String userId, Timestamp timestamp, String status,String CategoryId) {
        this.id = id;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.userId = userId;
        this.timestamp = timestamp;
        this.status = status;
        this.categoryId=CategoryId;

    }

    public Product(String id, String description, String imageUrl, String name, String price, String userId, String status, String categoryId) {
        this.id = id;
        this.description = description;
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.userId = userId;
        this.status = status;
        this.categoryId = categoryId;
    }

    // Getter và Setter cho tất cả các thuộc tính
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
