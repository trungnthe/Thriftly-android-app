package com.mastercoding.thriftly.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mastercoding.thriftly.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView productName, productPrice, productDescription;
    private ImageView productImage;
    private TextView productCategory;
    private Button buyButton,contactButton;

    private void bindingView() {
        // Đảm bảo các ID tương ứng với ID trong layout XML
        productName = findViewById(R.id.product_name_input);
        productPrice = findViewById(R.id.product_price_input);
        productImage = findViewById(R.id.product_detail_image);
        productDescription = findViewById(R.id.product_description_input);
        productCategory = findViewById(R.id.product_category);
        buyButton = findViewById(R.id.buy_button);
        contactButton = findViewById(R.id.contact_button);
    }

    private void bindingAction() {
        Intent intent = getIntent();
        String productId = intent.getStringExtra("product_id");
        Log.d("Product", "ID được chọn từ Intent: " + productId);
        loadProduct(productId);
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
                            String sellerId = document.getString("userId");

                            // Hiển thị thông tin lên giao diện
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
                            checkIfUserIsOwner(sellerId);
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
    // Hàm kiểm tra nếu người dùng hiện tại là chủ sở hữu
    private void checkIfUserIsOwner(String sellerId) {
        String currentUserId = getCurrentUserId();
        if (currentUserId != null && currentUserId.equals(sellerId)) {
            // Ẩn nút Contact và Buy nếu người dùng hiện tại là chủ sở hữu
            contactButton.setVisibility(View.GONE);
            buyButton.setVisibility(View.GONE);
        }
    }

    // Hàm lấy ID của người dùng hiện tại
    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();  // Trả về ID của người dùng hiện tại
        } else {
            return null;  // Trường hợp người dùng chưa đăng nhập
        }
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
