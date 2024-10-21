package com.mastercoding.thriftly.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mastercoding.thriftly.MainActivity;
import com.mastercoding.thriftly.R;

import java.util.HashMap;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {

    private EditText productNameInput;
    private EditText productPriceInput;
    private EditText productCategoryInput;
    private EditText productDescriptionInput;
    private Button saveButton;
    private FirebaseFirestore firestore;

    // Liên kết các view trong layout với mã nguồn
    private void bindingView() {
        productNameInput = findViewById(R.id.product_name_input);
        productPriceInput = findViewById(R.id.product_price_input);
        productCategoryInput = findViewById(R.id.product_category_input);
        productDescriptionInput = findViewById(R.id.product_description_input);
        saveButton = findViewById(R.id.save_button);
        firestore = FirebaseFirestore.getInstance();
    }
    private void loadProduct(){
        String productId = getIntent().getStringExtra("product_id");
        Log.d("productId", productId);

        if (productId == null) {
            Toast.makeText(this, "Không tìm thấy ID sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("Products").document(productId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            String description = document.getString("description");
                            String category = document.getString("category");
                            String price = document.getString("price");

                            if (name != null) {
                                productNameInput.setText(name);
                            }
                            if (description != null) {
                                productDescriptionInput.setText(description);
                            }
                            if (category != null) {
                                productCategoryInput.setText(category);
                            }
                            if (price != null) {
                                productPriceInput.setText(price);
                            }
                        } else {
                            Toast.makeText(EditProductActivity.this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (task.getException() != null) {
                            Toast.makeText(EditProductActivity.this, "Lỗi khi tải dữ liệu sản phẩm: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditProductActivity.this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(EditProductActivity.this, "Lỗi khi kết nối Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    // Cập nhật hành động cho các nút hoặc tương tác khác
    private void bindingAction() {
        saveButton.setOnClickListener(this::saveProduct);

    }

    // Hàm lưu dữ liệu sản phẩm sau khi cập nhật
    private void saveProduct(View view) {
        String updatedName = productNameInput.getText().toString().trim();
        String updatedPrice = productPriceInput.getText().toString().trim();
        String updatedCategory = productCategoryInput.getText().toString().trim();
        String updatedDescription = productDescriptionInput.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (updatedName.isEmpty() || updatedPrice.isEmpty() || updatedCategory.isEmpty() || updatedDescription.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nhận productId từ Intent để cập nhật sản phẩm trong Firestore
        String productId = getIntent().getStringExtra("product_id"); // Giả sử product_id đã được truyền qua Intent

        if (productId != null) {
            // Tạo một bản đồ chứa các trường cần cập nhật
            Map<String, Object> updatedProduct = new HashMap<>();
            updatedProduct.put("name", updatedName);
            updatedProduct.put("price", updatedPrice);
            updatedProduct.put("category", updatedCategory);
            updatedProduct.put("description", updatedDescription);

            // Cập nhật sản phẩm trực tiếp sử dụng productId
            firestore.collection("Products").document(productId).update(updatedProduct)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditProductActivity.this, "Thông tin sản phẩm đã được cập nhật", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProductActivity.this, MainActivity.class);
                        intent.putExtra("showFragment", "homeFragment");  // Truyền thông tin để hiển thị HomeFragment
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProductActivity.this, "Lỗi khi cập nhật sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin sản phẩm để cập nhật", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Thiết lập chế độ Edge-to-Edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bindingView();
        bindingAction();
        loadProduct();
    }
}
