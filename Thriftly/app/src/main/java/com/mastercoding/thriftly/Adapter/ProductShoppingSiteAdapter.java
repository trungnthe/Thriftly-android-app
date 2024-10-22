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

public class ProductShoppingSiteAdapter extends RecyclerView.Adapter<ProductShoppingSiteAdapter.VH> {

    private List<Product> data;
    private String currentUserId;

    public ProductShoppingSiteAdapter(List<Product> data, String currentUserId) {
        this.data = data;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ProductShoppingSiteAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.product_item_shopping, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductShoppingSiteAdapter.VH holder, int position) {
        Product product = data.get(position);
        holder.setData(product);
        // Chỉ hiện nút Edit nếu người dùng hiện tại là người tạo ra sản phẩm

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class VH extends RecyclerView.ViewHolder {

        private TextView tvProductName;
        private TextView tvProductPrice;
        private ImageView ivProductImage;
        private Product product;

        private void bindingView() {
            // Gán các view
            tvProductName = itemView.findViewById(R.id.product_name);
            tvProductPrice = itemView.findViewById(R.id.product_price);
            ivProductImage = itemView.findViewById(R.id.product_image);
        }

        private void onEditClick(View view) {
            Intent intent = new Intent(itemView.getContext(), EditProductActivity.class);
            intent.putExtra("product_id", product.getId());

            itemView.getContext().startActivity(intent);
        }



        public VH(@NonNull View itemView) {
            super(itemView);
            bindingView();
        }

        public void setData(Product product) {
            this.product = product;
            tvProductName.setText(product.getName());
            tvProductPrice.setText("₫ " + product.getPrice());

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
