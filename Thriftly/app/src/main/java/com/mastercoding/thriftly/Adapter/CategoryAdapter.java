package com.mastercoding.thriftly.Adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mastercoding.thriftly.Models.Category;
import com.mastercoding.thriftly.Models.Product;
import com.mastercoding.thriftly.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH>{

    private List<Category> data;
    private OnCategoryClickListener listener;

    public CategoryAdapter(List<Category> data, OnCategoryClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.category_item, parent, false);
        return new CategoryAdapter.VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.VH holder, int position) {
        Category category = data.get(position);
        holder.setData(category);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class VH extends RecyclerView.ViewHolder {

        private ImageView ivCategory;
        private Category category;

        private void bindingView() {

            ivCategory = itemView.findViewById(R.id.category_image);
        }

        private void bindingAction() {
            itemView.setOnClickListener(this::onSelectItem);
        }

        private void onSelectItem(View view) {
            listener.onCategoryClick(category);
        }


        public VH(@NonNull View itemView) {
            super(itemView);
            bindingView();
            bindingAction();
        }

        public void setData(Category category) {
            this.category = category;
            if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                Picasso.get()
                        .load(category.getImageUrl())
                        .placeholder(R.drawable.ic_logoapp)
                        .error(R.drawable.ic_logoapp)
                        .into(ivCategory);
            } else {
                Log.d("CategoryAdapter", "Image URL is null or empty");
                ivCategory.setImageResource(R.drawable.ic_logoapp); // Hiển thị ảnh placeholder mặc định
            }
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
}
