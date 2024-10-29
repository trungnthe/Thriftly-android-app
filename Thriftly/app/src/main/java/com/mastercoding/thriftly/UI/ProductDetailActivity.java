package com.mastercoding.thriftly.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mastercoding.thriftly.Models.Order;
import com.mastercoding.thriftly.Models.Product;
import com.mastercoding.thriftly.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {
    private Button buyButton;
    private LinearLayout modalContainer;
    private TextView productName, productPrice, productDescription;
    private ImageView productImage;
    private TextView productCategory;
    private Button confirmButton;
    private Button cancelButton;
    private FirebaseAuth auth;
    private Product currentProduct;

    private void bindingView() {
        // Đảm bảo các ID tương ứng với ID trong layout XML
        productName = findViewById(R.id.product_name_input);
        productPrice = findViewById(R.id.product_price_input);
        productImage = findViewById(R.id.product_detail_image);
        productDescription = findViewById(R.id.product_description_input);
        productCategory = findViewById(R.id.product_category);
        buyButton = findViewById(R.id.buy_button);
        confirmButton = findViewById(R.id.confirmButton);
        cancelButton = findViewById(R.id.cancelButton);
        modalContainer = findViewById(R.id.modalContainer);

        auth = FirebaseAuth.getInstance();
    }

    private void bindingAction() {
        Intent intent = getIntent();
        String productId = intent.getStringExtra("product_id");
        Log.d("Product", "ID được chọn từ Intent: " + productId);
        loadProduct(productId);
        buyButton.setOnClickListener(this::onBuyButtonClick);
        confirmButton.setOnClickListener(this::onConfirmButtonClick);
        cancelButton.setOnClickListener(v -> modalContainer.setVisibility(View.GONE));
    }

    private void onBuyButtonClick(View view) {
        // Kiểm tra xem sản phẩm đã được tải chưa
        if (currentProduct == null) {
            Toast.makeText(this, "Vui lòng đợi sản phẩm được tải", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị dialog xác nhận mua hàng
        modalContainer.setVisibility(View.VISIBLE); // Hiển thị modal
    }

    private void onConfirmButtonClick(View view) {

        // Lấy thông tin từ currentProduct
        String productId = currentProduct.getId();
        String sellerId = currentProduct.getUserId(); // Assuming "userId" is the seller ID
        double price = Double.parseDouble(currentProduct.getPrice());
        String userId = auth.getCurrentUser().getUid();

        // Tạo đối tượng chứa thông tin đơn hàng (sử dụng HashMap)
        Map<String, Object> order = new HashMap<>();
        order.put("buyerId", userId);
        order.put("orderDate", new Date());
        order.put("productId", productId);
        order.put("sellerId", sellerId);
        order.put("status", "waiting_confirmation");
        order.put("totalAmount", price); // Hoặc có thể lưu trữ dưới dạng double

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Thêm đơn hàng vào Firestore
        db.collection("Orders")
                .add(order)
                .addOnSuccessListener(documentReference -> {
                    Log.d("TAG", "Order added with ID: " + documentReference.getId());

                    // Cập nhật trạng thái sản phẩm thành "Sold" sau khi thêm đơn hàng thành công
                    db.collection("Products").document(productId)
                            .update("status", "Sold")
                            .addOnSuccessListener(aVoid -> {
                                Log.d("TAG", "Product updated successfully");
                                Toast.makeText(ProductDetailActivity.this, "Mua hàng thành công!", Toast.LENGTH_SHORT).show();
                                finish(); // Đóng activity sau khi mua hàng thành công
                            })
                            .addOnFailureListener(e -> {
                                Log.w("TAG", "Error updating product", e);
                                Toast.makeText(ProductDetailActivity.this, "Lỗi khi cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.w("TAG", "Error adding order", e);
                    Toast.makeText(ProductDetailActivity.this, "Lỗi khi tạo đơn hàng", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadProduct(String productId) {

        if (productId == null || productId.isEmpty()) {
            Toast.makeText(this, "ID sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Products").document(productId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            String price = document.getString("price");
                            String description = document.getString("description");
                            String imageUrl = document.getString("imageUrl");
                            String categoryId = document.getString("categoryId");
                            String sellerID = document.getString("userId");
                            String status = document.getString("status");
                            currentProduct = new Product(productId, description, imageUrl, name, price, sellerID, status, categoryId);
                            productName.setText(name != null ? name : "Tên sản phẩm không xác định");
                            productPrice.setText(price != null ? "₫ " + price : "Giá không xác định");
                            productDescription.setText(description != null ? description : "Mô tả không có sẵn");

                            // Hiển thị ảnh sản phẩm
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Picasso.get().load(imageUrl).placeholder(R.drawable.ic_logoapp).into(productImage);
                            } else {
                                productImage.setImageResource(R.drawable.ic_logoapp);
                            }

                            if (categoryId != null) {
                                loadCategory(categoryId);
                            } else {
                                productCategory.setText("Danh mục không xác định");
                            }
                        } else {
                            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Lỗi khi tải sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi kết nối Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadCategory(String categoryId) {
        if (categoryId == null || categoryId.isEmpty()) {
            productCategory.setText("Danh mục không xác định");
            return;
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Categories").document(categoryId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String categoryName = document.getString("categoryName");
                            productCategory.setText(categoryName != null ? categoryName : "Danh mục không xác định");
                        } else {
                            productCategory.setText("Không tìm thấy danh mục");
                        }
                    } else {
                        Toast.makeText(this, "Lỗi khi tải danh mục", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi kết nối Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bindingView();
        bindingAction();
    }
}
