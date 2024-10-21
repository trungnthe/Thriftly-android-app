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


import com.mastercoding.thriftly.Models.Product;
import com.mastercoding.thriftly.R;
import com.mastercoding.thriftly.UI.EditProductActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

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
        // Chỉ hiện nút Edit nếu người dùng hiện tại là người tạo ra sản phẩm
        if (product.getUserId().equals(currentUserId)) {
            holder.btnEdit.setVisibility(View.VISIBLE);
        } else {
            holder.btnEdit.setVisibility(View.GONE); // Ẩn nút Edit nếu người dùng không phải là người tạo sản phẩm
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
            tvProductName.setText(product.getName());
            tvProductPrice.setText("Price: " + product.getPrice() +" VND");
            tvProductDescription.setText(product.getDescription());
            tvCategoryName.setText(product.getCategory());

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
