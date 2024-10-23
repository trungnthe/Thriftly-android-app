package com.mastercoding.thriftly.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mastercoding.thriftly.R;
import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView productName, productPrice,productDescription, productCategory;
    private ImageView productImage;

    private void bindingView(){
        // Đảm bảo các ID tương ứng với ID trong layout XML
        productName = findViewById(R.id.product_name_input);
        productPrice = findViewById(R.id.product_price_input);
        productImage = findViewById(R.id.product_detail_image);
        productDescription = findViewById(R.id.product_description_input);
        productCategory = findViewById(R.id.product_category_input);
    }


    private void bindingAction(){
        Intent intent = getIntent();

        // Nhận các giá trị từ Intent
        String name = intent.getStringExtra("product_name");
        String price = intent.getStringExtra("product_price");
        String imageUrl = intent.getStringExtra("product_image");
        String description = intent.getStringExtra("product_description");
        String category = intent.getStringExtra("product_category");

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

        if (category != null) {
            productCategory.setText(category);
        } else {
            productCategory.setText("Danh mục không xác định");
        }

        // Hiển thị ảnh sản phẩm nếu có
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_logoapp).into(productImage);
        } else {
            productImage.setImageResource(R.drawable.ic_logoapp);  // Hiển thị ảnh mặc định nếu không có ảnh
        }
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