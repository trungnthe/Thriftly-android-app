package com.mastercoding.thriftly.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {

    private EditText productNameInput;
    private EditText productPriceInput;
    private Spinner productCategorySpinner;

    private EditText productDescriptionInput;
    private Button saveButton;
    private Button deleteButton;
    private FirebaseFirestore firestore;

    // Liên kết các view trong layout với mã nguồn
    private void bindingView() {
        productNameInput = findViewById(R.id.product_name_input);
        productPriceInput = findViewById(R.id.product_price_input);
        productCategorySpinner = findViewById(R.id.product_category_spinner);
        productDescriptionInput = findViewById(R.id.product_description_input);
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);
        firestore = FirebaseFirestore.getInstance();
    }
    // Tải dữ liệu sản phẩm từ Firestore
    private void loadProduct() {
        String productId = getIntent().getStringExtra("product_id");
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
                            String categoryId = document.getString("categoryId");  // Lấy categoryId thay vì categoryName
                            String price = document.getString("price");

                            if (name != null) {
                                productNameInput.setText(name);
                            }
                            if (description != null) {
                                productDescriptionInput.setText(description);
                            }
                            if (price != null) {
                                productPriceInput.setText(price);
                            }

                            // Lưu categoryId để sử dụng khi cài đặt Spinner
                            getIntent().putExtra("product_category_id", categoryId);
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

//commit dung xoa Activity di nua nhe

    // Cập nhật hành động cho các nút hoặc tương tác khác
    private void bindingAction() {
        saveButton.setOnClickListener(this::saveProduct);
        deleteButton.setOnClickListener(this::deleteProduct);


    }

    private void deleteProduct(View view) {
        String productId = getIntent().getStringExtra("product_id");

        if (productId != null) {
            firestore.collection("Products").document(productId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditProductActivity.this, "Sản phẩm đã được xóa", Toast.LENGTH_SHORT).show();
                        // Chuyển về MainActivity sau khi xóa thành công
                        Intent intent = new Intent(EditProductActivity.this, MainActivity.class);
                        intent.putExtra("showFragment", "homeFragment");  // Hiển thị HomeFragment sau khi xóa
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditProductActivity.this, "Lỗi khi xóa sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Không tìm thấy sản phẩm để xóa", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm lưu dữ liệu sản phẩm sau khi cập nhật
    private void saveProduct(View view) {
        String updatedName = productNameInput.getText().toString().trim();
        String updatedPrice = productPriceInput.getText().toString().trim();
        String updatedDescription = productDescriptionInput.getText().toString().trim();

        // Lấy `categoryId` từ Tag của Spinner
        String updatedCategoryId = (String) productCategorySpinner.getTag();

        // Kiểm tra dữ liệu đầu vào
        if (updatedName.isEmpty() || updatedPrice.isEmpty() || updatedCategoryId == null || updatedDescription.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nhận productId từ Intent để cập nhật sản phẩm trong Firestore
        String productId = getIntent().getStringExtra("product_id");

        if (productId != null) {
            // Tạo một bản đồ chứa các trường cần cập nhật
            Map<String, Object> updatedProduct = new HashMap<>();
            updatedProduct.put("name", updatedName);
            updatedProduct.put("price", updatedPrice);
            updatedProduct.put("categoryId", updatedCategoryId);  // Lưu `categoryId` thay vì `categoryName`
            updatedProduct.put("description", updatedDescription);

            // Cập nhật sản phẩm trong Firestore
            firestore.collection("Products").document(productId).update(updatedProduct)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditProductActivity.this, "Thông tin sản phẩm đã được cập nhật", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProductActivity.this, MainActivity.class);
                        intent.putExtra("showFragment", "homeFragment");  // Quay về trang chủ
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
        setupCategorySpinner();
    }
    private void setupCategorySpinner() {
        firestore.collection("Categories").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> categoryNames = new ArrayList<>();
                        List<String> categoryIds = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult()) {
                            String categoryName = document.getString("categoryName");
                            String categoryId = document.getId();  // Lấy ID của danh mục

                            categoryNames.add(categoryName);  // Thêm tên vào Spinner
                            categoryIds.add(categoryId);      // Lưu ID để sử dụng khi cần
                        }

                        // Tạo ArrayAdapter cho Spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productCategorySpinner.setAdapter(adapter);

                        // Lấy product_category_id từ Intent và cài đặt Spinner
                        String currentCategoryId = getIntent().getStringExtra("product_category_id");
                        if (currentCategoryId != null) {
                            int position = categoryIds.indexOf(currentCategoryId);
                            if (position != -1) {
                                productCategorySpinner.setSelection(position);
                            }
                        }

                        // Lưu categoryId tương ứng với categoryName được chọn
                        productCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String selectedCategoryId = categoryIds.get(position);  // Lấy categoryId của danh mục được chọn
                                productCategorySpinner.setTag(selectedCategoryId);  // Lưu categoryId vào Tag để sử dụng sau
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Không làm gì nếu không chọn danh mục
                            }
                        });
                    } else {
                        Toast.makeText(this, "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                    }
                });
    }




}
