package com.mastercoding.thriftly.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mastercoding.thriftly.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView productName, productPrice, productDescription;
    private ImageView productImage;
    private Spinner productCategory;

    private void bindingView() {
        // Đảm bảo các ID tương ứng với ID trong layout XML
        productName = findViewById(R.id.product_name_input);
        productPrice = findViewById(R.id.product_price_input);
        productImage = findViewById(R.id.product_detail_image);
        productDescription = findViewById(R.id.product_description_input);
        productCategory = findViewById(R.id.product_category_spinner);
    }

    private void bindingAction() {
        Intent intent = getIntent();

        // Nhận các giá trị từ Intent
        String name = intent.getStringExtra("product_name");
        String price = intent.getStringExtra("product_price");
        String imageUrl = intent.getStringExtra("product_image");
        String description = intent.getStringExtra("product_description");
        String categoryId = intent.getStringExtra("product_category_id"); // Lấy categoryId từ Intent

        // Kiểm tra và hiển thị dữ liệu
        if (name != null) {
            productName.setText(name);
        } else {
            productName.setText("Tên sản phẩm không xác định");
        }

        if (price != null) {
            productPrice.setText("₫ " + price);
        } else {
            productPrice.setText("Giá không xác định");
        }

        if (description != null) {
            productDescription.setText(description);
        } else {
            productDescription.setText("Mô tả không có sẵn");
        }
        if (categoryId != null) {
            // Gọi phương thức thiết lập Spinner và truyền categoryId
            setupCategorySpinner(categoryId);
        } else {
            Log.e("BindingAction", "categoryId không hợp lệ, không thể thiết lập Spinner.");
        }

        // Hiển thị ảnh sản phẩm nếu có
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_logoapp).into(productImage);
        } else {
            productImage.setImageResource(R.drawable.ic_logoapp);  // Hiển thị ảnh mặc định nếu không có ảnh
        }

        // Gọi phương thức thiết lập Spinner và truyền categoryId
        setupCategorySpinner(categoryId);
    }

    private void setupCategorySpinner(String selectedCategoryId) {
        List<String> categoryNames = new ArrayList<>();
        List<String> categoryIds = new ArrayList<>();

        Log.d("SetupSpinner", "ID được chọn từ Intent: " + selectedCategoryId);

        // Tải danh mục từ Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Categories").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String categoryName = document.getString("categoryName");
                            String categoryId = document.getId();

                            Log.d("Firestore", "Tên danh mục: " + categoryName + ", ID: " + categoryId);

                            if (categoryName != null) {
                                categoryNames.add(categoryName);
                                categoryIds.add(categoryId);
                            } else {
                                Log.e("Firestore", "Tên danh mục không hợp lệ: " + categoryId);
                            }
                        }

                        // Log danh sách categoryIds
                        Log.d("SetupSpinner", "Danh sách categoryIds: " + categoryIds.toString());

                        // Kiểm tra xem danh sách categoryNames có dữ liệu không
                        if (!categoryNames.isEmpty()) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            productCategory.setAdapter(adapter);

                            // Cài đặt lựa chọn ban đầu cho Spinner
                            if (selectedCategoryId != null) {
                                int position = categoryIds.indexOf(selectedCategoryId);
                                Log.d("SetupSpinner", "Tìm vị trí cho ID: " + selectedCategoryId + ", Kết quả: " + position);
                                if (position != -1) {
                                    productCategory.setSelection(position);
                                } else {
                                    Log.e("SetupSpinner", "Không tìm thấy vị trí cho ID đã chọn: " + selectedCategoryId);
                                }
                            }
                        } else {
                            Toast.makeText(this, "Không tìm thấy danh mục nào.", Toast.LENGTH_SHORT).show();
                        }

                        productCategory.setEnabled(false);
                    } else {
                        Toast.makeText(this, "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
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
