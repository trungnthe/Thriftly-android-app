package com.mastercoding.thriftly.Adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.firestore.FirebaseFirestore;
import com.mastercoding.thriftly.Models.Product;
import com.mastercoding.thriftly.R;
import com.mastercoding.thriftly.UI.EditProductActivity;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    private List<Product> data;
    private String currentUserId;

    public ProductAdapter(List<Product> data, String currentUserId) {
        this.data = data;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ProductAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.product_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.VH holder, int position) {
        Product product = data.get(position);
        holder.setData(product);
        holder.tvProductStatus.setText(product.getStatus());

        // Chỉ hiện nút Edit nếu người dùng hiện tại là người tạo ra sản phẩm
        if (product.getUserId().equals(currentUserId)) {
            holder.btnEdit.setVisibility(View.VISIBLE);
        } else {
            holder.btnEdit.setVisibility(View.GONE); // Ẩn nút Edit nếu người dùng không phải là người tạo sản phẩm
        }
        if ("Sold".equals(product.getStatus())) {
            holder.btnEdit.setVisibility(View.GONE); // Ẩn nút Action
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE); // Hiển thị nút Action nếu trạng thái không phải là Sold
        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class VH extends RecyclerView.ViewHolder {

        private TextView tvProductName;
        private TextView tvProductPrice;
        private TextView tvProductStatus;
        private ImageView ivProductImage;
        private TextView tvProductDescription;
        private TextView tvCategoryName;
        private Button btnEdit;
        private Product product;

        private void bindingView() {
            // Gán các view
            tvProductName = itemView.findViewById(R.id.product_name);
            tvProductPrice = itemView.findViewById(R.id.product_price);
            tvProductStatus = itemView.findViewById(R.id.product_status);
            ivProductImage = itemView.findViewById(R.id.product_image);
            tvProductDescription = itemView.findViewById(R.id.product_description);
            tvCategoryName = itemView.findViewById(R.id.product_category);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }

        private void bindingAction() {
            btnEdit.setOnClickListener(this::onEditClick);
        }

        private void onEditClick(View view) {
            Intent intent = new Intent(itemView.getContext(), EditProductActivity.class);
            intent.putExtra("product_id", product.getId());
            itemView.getContext().startActivity(intent);
        }



        public VH(@NonNull View itemView) {
            super(itemView);
            bindingView();
            bindingAction();
        }

        public void setData(Product product) {
            this.product = product;

            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            String formattedPrice = numberFormat.format(Long.parseLong(product.getPrice()));

            tvProductName.setText(product.getName());
            tvProductPrice.setText("Price: " + formattedPrice + " VND");
            tvProductDescription.setText(product.getDescription());

            if (product.getStatus() != null) {
                if (product.getStatus().equals("Sold")) {
                    tvProductStatus.setText("Status: Sold");
                    tvProductStatus.setTextColor(itemView.getResources().getColor(R.color.red));
                    btnEdit.setVisibility(View.GONE); // Ẩn nút Edit nếu sản phẩm đã bán
                } else if (product.getStatus().equals("Available")) {
                    tvProductStatus.setText("Status: Available");
                    tvProductStatus.setTextColor(itemView.getResources().getColor(R.color.lavender));
                    btnEdit.setVisibility(View.VISIBLE); // Hiển thị nút Edit nếu sản phẩm còn hàng
                } else {
                    tvProductStatus.setText("Status: Unknown");
                    btnEdit.setVisibility(View.VISIBLE);
                }
            } else {
                tvProductStatus.setText("Status: Unknown");
                btnEdit.setVisibility(View.VISIBLE);
            }

            // Kiểm tra và tải categoryName từ Firestore nếu chỉ có categoryId
            if (product.getCategoryId() != null) {
                FirebaseFirestore.getInstance().collection("Categories")
                        .document(product.getCategoryId())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String categoryName = documentSnapshot.getString("categoryName");
                                tvCategoryName.setText(categoryName);  // Hiển thị tên danh mục
                            } else {
                                tvCategoryName.setText("Unknown category");  // Hiển thị khi không tìm thấy danh mục
                            }
                        })
                        .addOnFailureListener(e -> {
                            tvCategoryName.setText("Unknown category");  // Xử lý lỗi
                            Log.d("ProductAdapter", "Error getting category: " + e.getMessage());
                        });
            } else {
                tvCategoryName.setText("Unknown category");
            }

            // Hiển thị ảnh sản phẩm
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Picasso.get()
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_logoapp)
                        .error(R.drawable.ic_logoapp)
                        .into(ivProductImage);
            } else {
                Log.d("ProductAdapter", "Image URL is null or empty");
                ivProductImage.setImageResource(R.drawable.ic_logoapp); // Hiển thị ảnh placeholder mặc định
            }
        }

    }
}
