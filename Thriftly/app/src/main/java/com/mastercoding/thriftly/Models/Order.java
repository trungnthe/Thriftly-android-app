package com.mastercoding.thriftly.Models;
import java.util.Date;

public class Order {
    private String buyerId;
    private Date orderDate;
    private String orderId;
    private String productId;
    private String sellerId;
    private String status;
    private double totalAmount;
    private String productName;   // New field
    private String imageUrl;
    private String buyerName;
    public Order() {
        // No-arg constructor for Firestore
    }

    public Order(String buyerId, Date orderDate, String orderId, String productId, String sellerId, String status, double totalAmount) {
        this.buyerId = buyerId;
        this.orderDate = orderDate;
        this.orderId = orderId;
        this.productId = productId;
        this.sellerId = sellerId;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public Order(String buyerId, Date orderDate, String productId, String sellerId, String orderId, String status, double totalAmount, String productName, String imageUrl, String buyerName) {
        this.buyerId = buyerId;
        this.orderDate = orderDate;
        this.productId = productId;
        this.sellerId = sellerId;
        this.orderId = orderId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.buyerName = buyerName;
    }

    // Getters and Setters
    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    @Override
    public String toString() {
        return "Order{" +
                "buyerId='" + buyerId + '\'' +
                ", orderDate=" + orderDate +
                ", orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
