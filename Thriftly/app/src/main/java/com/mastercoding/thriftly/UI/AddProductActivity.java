package com.mastercoding.thriftly.UI;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.mastercoding.thriftly.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mastercoding.thriftly.R;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private ImageView productImageView;
    private EditText productNameInput, productPriceInput, productDescriptionInput;
    private Button selectImageButton, addProductButton;
    private Spinner productCategorySpinner;


    // Firebase components
    private StorageReference storageRef;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    // Image URI
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Liên kết các thành phần UI
        bindingView();

        // Liên kết các hành động
        bindingAction();
        loadCategoriesFromFirestore();
    }

    // Liên kết các thành phần UI
    private void bindingView() {
        productImageView = findViewById(R.id.product_image);
        productNameInput = findViewById(R.id.product_name_input);
        productPriceInput = findViewById(R.id.product_price_input);
        productDescriptionInput = findViewById(R.id.product_description_input);
        selectImageButton = findViewById(R.id.select_image_button);
        addProductButton = findViewById(R.id.add_product_button);
        productCategorySpinner = findViewById(R.id.product_category_spinner);

        // Khởi tạo Firebase
        storageRef = FirebaseStorage.getInstance().getReference("products");
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    // Liên kết các hành động của các thành phần
    private void bindingAction() {
        selectImageButton.setOnClickListener(this::openImagePicker);
        addProductButton.setOnClickListener(this::uploadProduct);

    }

    // Mở thư viện ảnh để chọn ảnh
    private void openImagePicker(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Chỉ cho phép chọn ảnh
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh sản phẩm"), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 100 && data != null) {
            imageUri = data.getData();
            productImageView.setImageURI(imageUri); // Hiển thị ảnh đã chọn
        }
    }

    // Hàm upload sản phẩm, bao gồm upload ảnh
    private void uploadProduct(View view) {
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child("Products/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            addProductToFirestore(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddProductActivity.this, "Tải ảnh lên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Chưa chọn ảnh!", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm để lấy phần mở rộng của tệp ảnh
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // Lưu thông tin sản phẩm vào Firestore
    private void addProductToFirestore(String imageUrl) {
        String userId = auth.getCurrentUser().getUid();

        // Lấy ID của danh mục từ Spinner (sử dụng Tag đã set trước đó)
        String selectedCategoryId = (String) productCategorySpinner.getTag();

        // Tạo đối tượng chứa thông tin sản phẩm
        Map<String, Object> product = new HashMap<>();
        product.put("name", productNameInput.getText().toString());
        product.put("price", productPriceInput.getText().toString());
        product.put("description", productDescriptionInput.getText().toString());
        product.put("categoryId", selectedCategoryId);  // Thêm categoryId thay vì categoryName
        product.put("imageUrl", imageUrl); // Thêm URL ảnh
        product.put("userId", userId); // Thêm ID người dùng
        product.put("timestamp", Timestamp.now()); // Thêm ngày đăng
        product.put("status", "Available");

        // Thêm thông tin sản phẩm vào Firestore
        firestore.collection("Products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddProductActivity.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddProductActivity.this, MainActivity.class);
                    intent.putExtra("showFragment", "homeFragment");  // Truyền thông tin để hiển thị HomeFragment
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddProductActivity.this, "Thêm sản phẩm thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadCategoriesFromFirestore() {
        firestore.collection("Categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> categoryNames = new ArrayList<>();
                        List<String> categoryIds = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult()) {
                            // Lấy tên category và ID từ Firestore
                            String categoryName = document.getString("categoryName");
                            String categoryId = document.getId();  // Lấy ID của tài liệu làm categoryId

                            categoryNames.add(categoryName);  // Thêm tên danh mục vào danh sách
                            categoryIds.add(categoryId);      // Thêm ID danh mục vào danh sách
                        }

                        // Gán dữ liệu vào Spinner hiển thị tên danh mục
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productCategorySpinner.setAdapter(adapter);

                        // Khi danh mục được chọn, lấy ID của danh mục
                        productCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                // Lưu trữ ID của danh mục đã chọn
                                String selectedCategoryId = categoryIds.get(position);
                                // Lưu selectedCategoryId để sử dụng khi thêm sản phẩm
                                productCategorySpinner.setTag(selectedCategoryId);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Không có danh mục nào được chọn
                            }
                        });
                    } else {
                        Toast.makeText(this, "Không thể tải danh mục: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
