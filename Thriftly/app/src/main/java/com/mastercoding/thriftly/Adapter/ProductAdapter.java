package com.mastercoding.thriftly.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mastercoding.thriftly.Models.Product;
import com.mastercoding.thriftly.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    private List<Product> data;

    public ProductAdapter(List<Product> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ProductAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.VH holder, int position) {
        Product product = data.get(position);
        holder.setData(product);
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

        private void bindingView() {
            // Gán các view
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductStatus = itemView.findViewById(R.id.tvProductStatus);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
        }

        private void bindingAction() {
        }


        public VH(@NonNull View itemView) {
            super(itemView);
            bindingView();
            bindingAction();
        }

        public void setData(Product product) {
            tvProductName.setText(product.getName());
            tvProductPrice.setText("Price: $" + product.getPrice());
            tvProductStatus.setText(product.getStatus());

            Picasso.get()
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_logoapp)
                    .error(R.drawable.ic_logoapp)
                    .into(ivProductImage);
        }
    }
}
