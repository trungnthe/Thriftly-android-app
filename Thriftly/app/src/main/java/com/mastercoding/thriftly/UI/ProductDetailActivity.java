package com.mastercoding.thriftly.UI;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mastercoding.thriftly.Models.Product;
import com.mastercoding.thriftly.Chat.AndroidUtil;
import com.mastercoding.thriftly.Chat.ChatActivity;
import com.mastercoding.thriftly.Models.UserModel;
import com.mastercoding.thriftly.Notification.NotificationService;
import com.mastercoding.thriftly.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
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
    private Button contactButton;
    private String sellerId;

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
        buyButton = findViewById(R.id.buy_button);
        contactButton = findViewById(R.id.contact_button);
    }

    private void bindingAction() {
        contactButton.setOnClickListener(this::onContactButtonClick);;
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
        // Kiểm tra xem sản phẩm đã được tải chưa
        if (currentProduct == null) {
            Toast.makeText(this, "Sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        String productId = currentProduct.getId();
        String sellerId = currentProduct.getUserId(); // sellerId của người bán
        double price = Double.parseDouble(currentProduct.getPrice());
        String userId = auth.getCurrentUser().getUid();

        // Tạo đối tượng chứa thông tin đơn hàng
        Map<String, Object> order = new HashMap<>();
        order.put("buyerId", userId);
        order.put("orderDate", new Date());
        order.put("productId", productId);
        order.put("sellerId", sellerId);
        order.put("status", "waiting_confirmation");
        order.put("totalAmount", price);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lưu đơn hàng vào Firestore
        db.collection("Orders")
                .add(order)
                .addOnSuccessListener(documentReference -> {
                    Log.d("TAG", "Order added with ID: " + documentReference.getId());

                    // Cập nhật trạng thái sản phẩm thành "Sold"
                    db.collection("Products").document(productId)
                            .update("status", "Sold")
                            .addOnSuccessListener(aVoid -> {
                                Log.d("TAG", "Product updated successfully");

                                // Truy xuất fcmToken của người bán và gửi thông báo
                                db.collection("User").document(sellerId)
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                String fcmToken = documentSnapshot.getString("fcmToken");
                                                if (fcmToken != null && !fcmToken.isEmpty()) {
                                                    // Gọi NotificationService để gửi thông báo
                                                    Log.d("FCM", "Sending notification to fcmToken: " + fcmToken); // Log fcmToken
                                                    NotificationService.sendNotification(fcmToken, "Đơn hàng mới", "Bạn có một đơn hàng mới cho sản phẩm: " + currentProduct.getName());
                                                    Toast.makeText(ProductDetailActivity.this, "Thông báo đã được gửi tới người bán!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(ProductDetailActivity.this, "Người bán không có FCM Token", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(ProductDetailActivity.this, "Không tìm thấy thông tin người bán", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(ProductDetailActivity.this, "Lỗi khi lấy thông tin người bán", Toast.LENGTH_SHORT).show());

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



    private void onContactButtonClick(View view) {
        if (sellerId != null) {
            fetchSellerAndStartChat(sellerId);
        } else {
            Toast.makeText(this, "Seller information is missing", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to fetch seller details and start chat
    private void fetchSellerAndStartChat(String sellerId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("User").document(sellerId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            UserModel seller = document.toObject(UserModel.class); // Deserialize to UserModel
                            if (seller != null) {
                                Intent intent = new Intent(this, ChatActivity.class);
                                AndroidUtil.passUserModelAsIntent(intent, seller); // Pass seller model
                                startActivity(intent);
                            } else {
                                Toast.makeText(this, "Failed to load seller information", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Seller not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error loading seller data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Firestore connection error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            sellerId = document.getString("userId");
                            String status = document.getString("status");
                            currentProduct = new Product(productId, description, imageUrl, name, price, sellerId, status, categoryId);


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
